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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;
import com.db4o.io.FileStorage;
import com.db4o.io.NonFlushingStorage;

public class PersistenceEngineDb4o implements PersistenceEngine {

	protected String directory;
	protected String filename;
	protected ObjectContainer db;

	public void open(String database) {
		initDirectory(database);
		openEngine();
	}

	protected void initDirectory(String database) {
		if (database.contains(File.separator)) {
			directory = database.endsWith(File.separator) ? database : database
					+ File.separator;
		} else {
			directory = System.getProperty("user.home") + File.separator
					+ "db4o" + File.separator + database + File.separator;
		}
		filename = directory + "db4o.yap";
		new File(directory).mkdirs();
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

	public String getBlobDirectory() {
		return directory;
	}

	public Collection<Object> query() {
		List<Object> objects = new ArrayList<>();
		for (Object obj : db.queryByExample(null)) {
			db.ext().refresh(obj, 1);
			objects.add(obj);
		}
		return objects;
	}

	public void save(Object object) {
		db.store(object);
	}

	public void delete(Object object) {
		db.delete(object);
	}

	public void begin() {
		// Empty as db4o don't needs an explicit transaction begin.
	}

	public void commit() {
		db.commit();
	}

	public void rollback() {
		db.rollback();
	}

	public void backup() {
		String backupFilename = filename
				+ new SimpleDateFormat("-yyyyMMdd").format(new Date());
		db.ext().backup(backupFilename);
	}

	public void close() {
		db.close();
	}

	public String toString() {
		return filename;
	}
}
