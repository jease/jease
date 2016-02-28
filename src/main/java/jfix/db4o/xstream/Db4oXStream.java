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

import com.db4o.Db4oEmbedded;
import com.db4o.ObjectContainer;
import com.db4o.config.EmbeddedConfiguration;

public class Db4oXStream {

	private String databaseFile;
	private String xstreamFile;

	public Db4oXStream(String databaseFile, String xstreamFile) {
		this.databaseFile = databaseFile;
		this.xstreamFile = xstreamFile;
	}

	public void toXML() {
		EmbeddedConfiguration config = Db4oEmbedded.newConfiguration();
		config.common().activationDepth(Integer.MAX_VALUE);
		ObjectContainer db4o = Db4oEmbedded.openFile(config, databaseFile);
		new XStreamFile(xstreamFile).write(db4o.queryByExample(null));
		db4o.close();
	}

	public void fromXML() {
		ObjectContainer db4o = Db4oEmbedded.openFile(databaseFile);
		for (Object obj : new XStreamFile(xstreamFile).read()) {
			db4o.store(obj);
		}
		db4o.commit();
		db4o.close();
	}
}
