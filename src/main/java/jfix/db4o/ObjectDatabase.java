/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jfix.db4o;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import jfix.db4o.engine.PersistenceEngine;
import jfix.util.Reflections;

public class ObjectDatabase {

	public static int MAINTENANCE_INTERVAL = 1 * 3600 * 1000;

	private ReadWriteLock lock;
	private ObjectRepository objectRepository;
	private PersistenceEngine persistenceEngine;
	private Timer maintenanceTimer;
	private boolean maintenanceScheduled;
	private boolean transactionInProgress;
	private boolean supplierCacheRetention;
	private Map<Supplier<?>, Object> supplierCache;
	private List<Blob> blobsToSave;
	private List<Blob> blobsToDelete;
	private long timestamp;

	public ObjectDatabase(PersistenceEngine persistenceEngine) {
		this.lock = new ReentrantReadWriteLock();
		this.objectRepository = new ObjectRepository();
		this.supplierCache = new IdentityHashMap<>();
		this.persistenceEngine = persistenceEngine;
	}

	public PersistenceEngine getPersistenceEngine() {
		return persistenceEngine;
	}

	public void open() {
		lock.writeLock().lock();
		try {
			maintenanceScheduled = false;
			transactionInProgress = false;
			supplierCacheRetention = false;
			populateObjectRepository();
			startMaintenanceTimer(MAINTENANCE_INTERVAL);
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void populateObjectRepository() {
		String blobDirectory = getBlobDirectory();
		for (Object obj : persistenceEngine.query()) {
			if (obj instanceof Persistent) {
				objectRepository.put(obj);
				if (obj instanceof Blob) {
					((Blob) obj).initPath(blobDirectory);
				}
			}
		}
	}

	private void startMaintenanceTimer(int period) {
		maintenanceTimer = new Timer(true);
		maintenanceTimer.schedule(new TimerTask() {
			public void run() {
				lock.writeLock().lock();
				try {
					if (maintenanceScheduled) {
						gc();
						persistenceEngine.backup();
					}
				} finally {
					maintenanceScheduled = false;
					lock.writeLock().unlock();
				}
			}
			// Minimize IO by distributing backups for different databases
		}, (int) (Math.random() * period), period);
	}

	private void stopMaintenanceTimer() {
		if (maintenanceTimer != null) {
			maintenanceTimer.cancel();
			maintenanceTimer = null;
		}
	}

	public String getBlobDirectory() {
		return persistenceEngine.getBlobDirectory();
	}

	public long getTimestamp() {
		lock.readLock().lock();
		try {
			return timestamp;
		} finally {
			lock.readLock().unlock();
		}
	}

	public void close() {
		lock.writeLock().lock();
		try {
			stopMaintenanceTimer();
			if (persistenceEngine != null) {
				persistenceEngine.close();
			}
			persistenceEngine = null;
			objectRepository = null;
			supplierCache = null;
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void gc() {
		lock.writeLock().lock();
		try {
			Set<Object> orphanedValues = objectRepository
					.getGarbage(Persistent.Value.class);
			for (Object orphanedValue : orphanedValues) {
				if (orphanedValue instanceof Persistent.Value) {
					deleteDeliberately((Persistent) orphanedValue);
				}
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public <E> List<E> query(Class<E> clazz) {
		lock.readLock().lock();
		try {
			return (List<E>) objectRepository.get(clazz);
		} finally {
			lock.readLock().unlock();
		}
	}

	public <E> List<E> query(Class<E> clazz, Predicate<E> predicate) {
		lock.readLock().lock();
		try {
			return ((List<E>) objectRepository.get(clazz)).stream()
					.filter(predicate).collect(Collectors.toList());
		} finally {
			lock.readLock().unlock();
		}
	}

	public <E> E queryUnique(Class<E> clazz, Predicate<E> predicate) {
		lock.readLock().lock();
		try {
			List<E> result = query(clazz, predicate);
			if (result == null || result.size() != 1) {
				return null;
			}
			return result.get(0);
		} finally {
			lock.readLock().unlock();
		}
	}

	public <E> boolean isUnique(E entity, Predicate<E> predicate) {
		lock.readLock().lock();
		try {
			List<E> result = query((Class<E>) entity.getClass(), predicate);
			if (result == null) {
				return true;
			}
			if (result.size() == 1 && result.get(0) != entity) {
				return false;
			}
			if (result.size() > 1) {
				return false;
			}
			return true;
		} finally {
			lock.readLock().unlock();
		}
	}

	public boolean isStored(Persistent object) {
		lock.readLock().lock();
		try {
			return objectRepository.get(object.getClass()).contains(object);
		} finally {
			lock.readLock().unlock();
		}
	}

	public List<Persistent> queryReferrers(Persistent reference) {
		lock.readLock().lock();
		try {
			List<Persistent> result = new ArrayList<>();
			for (Object referrer : objectRepository.getReferrers(reference)) {
				result.add((Persistent) referrer);
			}
			return result;
		} finally {
			lock.readLock().unlock();
		}
	}

	public <E> E query(Supplier<E> supplier) {
		lock.readLock().lock();
		try {
			E result = (E) supplierCache.get(supplier);
			if (result == null) {
				result = supplier.get();
				supplierCache.put(supplier, result);
			}
			return result;
		} finally {
			lock.readLock().unlock();
		}
	}

	public void read(Runnable transaction) {
		lock.readLock().lock();
		try {
			transaction.run();
		} finally {
			lock.readLock().unlock();
		}
	}

	public void write(Runnable transaction) {
		lock.writeLock().lock();
		try {
			persistenceEngine.begin();
			transactionInProgress = true;
			transaction.run();
			if (blobsToSave != null) {
				String blobDirectory = getBlobDirectory();
				for (Blob blob : blobsToSave) {
					blob.initPath(blobDirectory);
				}
			}
			if (blobsToDelete != null) {
				for (Blob blob : blobsToDelete) {
					blob.getFile().delete();
				}
			}
			persistenceEngine.commit();
		} catch (Throwable e) {
			persistenceEngine.rollback();
			close();
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			blobsToSave = null;
			blobsToDelete = null;
			maintenanceScheduled = true;
			transactionInProgress = false;
			timestamp = System.currentTimeMillis();
			if (supplierCache != null && !supplierCacheRetention) {
				supplierCache.clear();
			}
			lock.writeLock().unlock();
		}
	}

	/**
	 * Save given object to storage and clear the supplier cache after the
	 * operation.
	 */
	public void save(final Persistent persistent) {
		lock.writeLock().lock();
		try {
			if (transactionInProgress) {
				traverseAndSave(persistent);
			} else {
				write(new Runnable() {
					public void run() {
						save(persistent);
					}
				});
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	/**
	 * Persist given object to storage without clearing the supplier cache.
	 */
	public void persist(final Persistent persistent) {
		lock.writeLock().lock();
		try {
			supplierCacheRetention = true;
			save(persistent);
		} finally {
			supplierCacheRetention = false;
			lock.writeLock().unlock();
		}
	}

	public void delete(final Persistent persistent) {
		lock.writeLock().lock();
		try {
			int refcount = queryReferrers(persistent).size();
			if (refcount != 0) {
				// Garbage collection to remove possible dangling references by
				// values.
				gc();
				refcount = queryReferrers(persistent).size();
				if (refcount != 0) {
					throw new RuntimeException("Deletion not possible: \""
							+ String.valueOf(persistent)
							+ "\" is still referenced by " + refcount
							+ " referrers.");
				}
			}
			if (transactionInProgress) {
				traverseAndDelete(persistent);
			} else {
				write(new Runnable() {
					public void run() {
						delete(persistent);
					}
				});
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	public void deleteDeliberately(final Persistent persistent) {
		lock.writeLock().lock();
		try {
			if (transactionInProgress) {
				traverseAndDelete(persistent);
			} else {
				write(new Runnable() {
					public void run() {
						deleteDeliberately(persistent);
					}
				});
			}
		} finally {
			lock.writeLock().unlock();
		}
	}

	private void traverseAndSave(Object candidate) {
		if (candidate != null) {
			traverseAndExecute(candidate, new Consumer<Object>() {
				public void accept(Object object) {
					traverseAndSave(object);
				}
			});
			if (candidate instanceof Persistent) {
				objectRepository.put(candidate);
				persistenceEngine.save(candidate);
				if (candidate instanceof Blob) {
					if (blobsToSave == null) {
						blobsToSave = new ArrayList<>();
					}
					blobsToSave.add((Blob) candidate);
				}
				return;
			}
		}
	}

	private void traverseAndDelete(Object candidate) {
		if (candidate != null) {
			// Don't cascade delete on values.
			if (!(candidate instanceof Persistent.Value)) {
				traverseAndExecute(candidate, new Consumer<Object>() {
					public void accept(Object object) {
						traverseAndDelete(object);
					}
				});
			}
			if (candidate instanceof Persistent) {
				objectRepository.remove(candidate);
				persistenceEngine.delete(candidate);
				if (candidate instanceof Blob) {
					if (blobsToDelete == null) {
						blobsToDelete = new ArrayList<>();
					}
					blobsToDelete.add((Blob) candidate);
				}
			}
		}
	}

	private void traverseAndExecute(Object candidate, Consumer<Object> consumer) {
		try {
			for (Field field : Reflections.getFields(candidate.getClass())) {
				executeOnValues(field.get(candidate), consumer);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	private void executeOnValues(Object candidate, Consumer<Object> consumer) {
		if (candidate == null) {
			return;
		}
		if (candidate instanceof Persistent.Value || candidate instanceof Blob) {
			consumer.accept(candidate);
			return;
		}
		if (candidate instanceof Persistent.Value[]
				|| candidate instanceof Blob[]) {
			for (Object arrayItem : (Object[]) candidate) {
				executeOnValues(arrayItem, consumer);
			}
			return;
		}
	}
}
