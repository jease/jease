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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.FileStorage;
import com.db4o.io.NonFlushingStorage;

public class PersistenceEngineDb4o extends PersistenceEngineBase implements PersistenceEngine {

	protected ObjectContainer db;

	@Override
    protected String getEngineName() {
        return "db4o";
    }

    @Override
    protected String getEngineFileName() {
        return "db4o.yap";
    }

	@Override
    public void open(String database) {
		initDirectory(database);
		openEngine();
	}

	protected void openEngine() {
		db = Db4oEmbedded.openFile(newConfiguration(), filename);
	}

	protected EmbeddedConfiguration newConfiguration() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().activationDepth(0);
		config.common().allowVersionUpdates(true);
		config.common().callConstructors(true);
		config.common().callbacks(false);
		config.common().weakReferences(false);
		config.file().freespace().useRamSystem();
		config.file().storage(new NonFlushingStorage(new FileStorage()));
		return config;
	}

	@Override
    public Collection<Object> query() {
		List<Object> objects = new ArrayList<>();
		for (Object obj : db.queryByExample(null)) {
			db.ext().refresh(obj, 1);
			objects.add(obj);
		}
		return objects;
	}

	@Override
    public void save(Object object) {
		db.store(object);
	}

	@Override
    public void delete(Object object) {
		db.delete(object);
	}

	@Override
    public void begin() {
		// Empty as db4o don't needs an explicit transaction begin.
	}

	@Override
    public void commit() {
		db.commit();
	}

	@Override
    public void rollback() {
		db.rollback();
	}

	@Override
    public void backup() {
		String backupFilename = getBackupFileName();
		db.ext().backup(backupFilename);
	}

	@Override
    public void close() {
		db.close();
	}

}
