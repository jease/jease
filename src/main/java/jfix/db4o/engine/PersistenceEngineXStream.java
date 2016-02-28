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
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This implemenation is ONLY intended for debug-purposes.
 * 
 * @author mjablonski
 */
public class PersistenceEngineXStream implements PersistenceEngine {

	protected String directory;
	protected String filename;
	protected Set<Object> objects;
	protected Set<Object> objectsToSave;
	protected Set<Object> objectsToDelete;

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
					+ "xstream" + File.separator + database + File.separator;
		}
		filename = directory + "odb.xml";
		new File(directory).mkdirs();
	}

	protected void openEngine() {
		objects = new HashSet<>();
		objectsToSave = new HashSet<>();
		objectsToDelete = new HashSet<>();
	}

	public String getBlobDirectory() {
		return directory;
	}

	public Collection<Object> query() {
		try {
			if (new File(filename).exists()) {
				XStream xstream = new XStream(new DomDriver());
				xstream.setMode(XStream.ID_REFERENCES);
				FileReader reader = new FileReader(filename);
				objects = (Set<Object>) xstream.fromXML(reader);
				reader.close();
			}
			return objects;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void save(Object object) {
		objectsToSave.add(object);
	}

	public void delete(Object object) {
		objectsToDelete.add(object);
	}

	public void begin() {
		// Empty as XStream has no concept of transactions at all.
	}

	public void commit() {
		objects.addAll(objectsToSave);
		objects.removeAll(objectsToDelete);
		objectsToSave.clear();
		objectsToDelete.clear();
	}

	public void rollback() {
		objectsToSave.clear();
		objectsToDelete.clear();
	}

	public void backup() {
		try {
			String backupFilename = filename
					+ new SimpleDateFormat("-yyyyMMdd").format(new Date());
			FileUtils.copyFile(new File(filename), new File(backupFilename));
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void close() {
		try {
			XStream xstream = new XStream(new DomDriver());
			xstream.setMode(XStream.ID_REFERENCES);
			FileWriter writer = new FileWriter(filename);
			xstream.toXML(objects, writer);
			writer.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public String toString() {
		return filename;
	}
}
