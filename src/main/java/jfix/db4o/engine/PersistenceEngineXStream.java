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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * This implemenation is ONLY intended for debug-purposes.
 *
 * @author mjablonski
 */
public class PersistenceEngineXStream extends PersistenceEngineBase implements PersistenceEngine {

	protected String directory;
	protected String filename;
	protected final Set<Object> objects = new HashSet<>();
	protected final Set<Object> added   = new HashSet<>();
	protected final Set<Object> deleted = new HashSet<>();

	@Override
    protected String getEngineName() {
        return "xstream";
    }

    @Override
    protected String getEngineFileName() {
        return "odb.xml";
    }

	@Override
    public void open(String database) {
		initDirectory(database);
		openEngine();
	}

	protected void clearObjects() {
	    objects.clear();
        added.clear();
        deleted.clear();
	}

	protected void openEngine() {
	    clearObjects();
	}

	@Override
    public Collection<Object> query() {
		try {
			if (new File(filename).exists()) {
				XStream xstream = new XStream(new DomDriver());
				xstream.setMode(XStream.ID_REFERENCES);
				FileReader reader = new FileReader(filename);
				Object r = xstream.fromXML(reader);
				clearObjects();
				if (r instanceof Collection) {
				    @SuppressWarnings("unchecked")
                    Collection<Object> c = (Collection<Object>) r;
				    objects.addAll(c);
				} else {
				    objects.add(r);
				}
				reader.close();
			}
			return objects;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	@Override
    public void save(Object object) {
	    if (objects.contains(object)) return;
		objects.add(object);
		added.add(object);
	}

	@Override
    public void delete(Object object) {
	    if (objects.contains(object)) {
	        objects.remove(object);
	        if (added.contains(object)) added.remove(object);
	        else deleted.add(object);
	    }
	}

	@Override
    public void begin() {
		// Empty as XStream has no concept of transactions at all.
	}

	@Override
    public void commit() {
	    added.clear();
        deleted.clear();
	}

	@Override
    public void rollback() {
	    if (!added.isEmpty()) {
            for (Object i : added) objects.remove(i);
        }
        if (!deleted.isEmpty()) {
            for (Object i : deleted) objects.add(i);
        }
        added.clear();
        deleted.clear();
	}

	@Override
    public void backup() {
        backupAsCopyFile();
	}

	@Override
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

}
