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

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * Wrapper around XStream to write/retrieve collection of objects to/from
 * specified file.
 */
public class XStreamFile {

	private String filename;

	public XStreamFile(String filename) {
		this.filename = filename;
	}

	/**
	 * Writes objects to XML-File.
	 */
	public void write(Collection<Object> objects) {
		try {
			XStream xstream = new XStream(new DomDriver());
			xstream.setMode(XStream.ID_REFERENCES);
			FileWriter writer = new FileWriter(filename);
			xstream.toXML(new ArrayList<>(objects), writer);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Reads collection from XML-File.
	 */
	public Collection<Object> read() {
		try {
			XStream xstream = new XStream(new DomDriver());
			xstream.setMode(XStream.ID_REFERENCES);
			FileReader reader = new FileReader(filename);
			List<Object> objects = (List<Object>) xstream.fromXML(reader);
			reader.close();
			return objects;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
