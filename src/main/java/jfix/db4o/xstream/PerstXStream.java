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
package jfix.db4o.xstream;

import java.util.Set;

import org.garret.perst.Storage;
import org.garret.perst.StorageFactory;

public class PerstXStream {

	private String databaseFile;
	private String xstreamFile;

	public PerstXStream(String databaseFile, String xstreamFile) {
		this.databaseFile = databaseFile;
		this.xstreamFile = xstreamFile;
	}

	public void toXML() {
		Storage db = StorageFactory.getInstance().createStorage();
		db.open(databaseFile);
		new XStreamFile(xstreamFile).write((Set<Object>) db.getRoot());
		db.close();
	}

	public void fromXML() {
		Storage db = StorageFactory.getInstance().createStorage();
		db.open(databaseFile);
		db.setRoot(db.createSet());
		for (Object obj : new XStreamFile(xstreamFile).read()) {
			((Set<Object>) db.getRoot()).add(obj);
			db.store(obj);
		}
		db.commit();
		db.close();
	}
}
