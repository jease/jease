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
package jease.cmf.service;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.nio.file.Files;

import jease.cmf.domain.Node;
import jfix.db4o.Blob;

public class Revisioner extends Serializer {

	/**
	 * Create a new Revisioner. The service needs to know about all nodes which
	 * exist to configure the serializer properly.
	 */
	public Revisioner(Node... nodes) {
		for (Node node : nodes) {
			for (Field field : getFields(node)) {
				if (isNotSerialized(field)
						|| (isNodeDeclaringClass(field) && isNode(field))) {
					omitField(field);
					continue;
				}
				if (!isNodeDeclaringClass(field) && isNode(field)) {
					registerConverter(field);
				}
			}
		}
	}

	/**
	 * Serialize given Node into XML and return result as Blob.
	 */
	public Blob toBlob(Node node) {
		if (node == null) {
			return null;
		}
		try {
			Blob blob = new Blob();
			Writer writer = Files.newBufferedWriter(blob.getFile().toPath());
			toXML(node, writer);
			writer.close();
			return blob;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Deserializes given XML contained in Blob into Node.
	 */
	public Node fromBlob(Blob blob) {
		if (blob == null) {
			return null;
		}
		try {
			Reader reader = Files.newBufferedReader(blob.getFile().toPath());
			Node node = fromXML(reader);
			reader.close();
			return node;
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
