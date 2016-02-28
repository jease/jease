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
package jfix.db4o.engine.migration;

import java.io.File;
import java.io.IOException;

import jfix.db4o.ObjectDatabase;
import jfix.db4o.Persistent;
import jfix.db4o.engine.PersistenceEngine;
import jfix.db4o.engine.PersistenceEngineDb4o;
import jfix.db4o.engine.PersistenceEnginePerst;

import org.apache.commons.io.FileUtils;

public class Db4oToPerst {

	public static void main(String[] args) throws IOException {
		if (args.length == 0) {
			System.out.println("Database name required.");
			return;
		}

		String databaseName = args[0];

		PersistenceEngine db4oEngine = new PersistenceEngineDb4o();
		db4oEngine.open(databaseName);

		ObjectDatabase db4o = new ObjectDatabase(db4oEngine);
		db4o.open();

		PersistenceEngine perstEngine = new PersistenceEnginePerst();
		perstEngine.open(databaseName);

		ObjectDatabase perst = new ObjectDatabase(perstEngine);
		perst.open();

		for (Persistent p : db4o.query(Persistent.class)) {
			perst.save(p);
		}

		FileUtils.copyDirectory(new File(db4o.getBlobDirectory()
				+ File.separator + "blob"), new File(perst.getBlobDirectory()
				+ File.separator + "blob"));

		perst.close();
		db4o.close();
	}
}
