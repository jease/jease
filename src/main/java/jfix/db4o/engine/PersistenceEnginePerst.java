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
package jfix.db4o.engine;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

public class PersistenceEnginePerst implements PersistenceEngine {

	protected String directory;
	protected String filename;
	protected Storage db;
	protected Set<Object> root;

	public void open(String database) {
		initDirectory(database);
		openEngine();
	}

	protected void initDirectory(String database) {
		if (database.contains(File.separator)) {
			directory = database.endsWith(File.separator) ? database : database + File.separator;
		} else {
			directory = System.getProperty("user.home") + File.separator + "perst" + File.separator + database
					+ File.separator;
		}
		filename = directory + "perst.odb";
		new File(directory).mkdirs();
	}

	protected void openEngine() {
		db = StorageFactory.getInstance().createStorage();
		db.setProperty("perst.file.noflush", Boolean.TRUE);
		db.setProperty("perst.object.cache.kind", "strong");
		db.open(filename);
		if (db.getRoot() == null) {
			root = db.createSet();
			db.setRoot(root);
		} else {
			root = (Set<Object>) db.getRoot();
		}
	}

	public String getBlobDirectory() {
		return directory;
	}

	public Collection<Object> query() {
		return new HashSet<>(root);
	}

	public void save(Object object) {
		root.add(object);
		db.modify(object);
	}

	public void delete(Object object) {
		root.remove(object);
		db.deallocate(object);
		db.gc();
	}

	public void begin() {
		// Empty as Perst don't needs an explicit transaction begin.
	}

	public void commit() {
		db.commit();
	}

	public void rollback() {
		db.rollback();
	}

	public void backup() {
		try {
			String backupFilename = filename + new SimpleDateFormat("-yyyyMMdd").format(new Date());
			db.backup(new FileOutputStream(new File(backupFilename)));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void close() {
		db.close();
	}

	public String toString() {
		return filename;
	}
}
