/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;

import jease.cmf.domain.Node;
import jfix.util.Files;
import jfix.util.Validations;

/**
 * Service for dumping/restoring nodes into/from XML.
 */
public class Backup extends Serializer {

	/**
	 * Create a new Backup-Service. The service needs to know about all nodes
	 * which exist to configure the serializer properly.
	 */
	public Backup(Node... nodes) {
		for (Node node : nodes) {
			for (Field field : getFields(node)) {
				if (isNotSerialized(field)) {
					omitField(field);
					continue;
				}
				if (!isNodeDeclaringClass(field) && isNode(field)) {
					registerConverter(field);
					continue;
				}
			}
		}
	}

	/**
	 * Dump contents of node (and all children) into a file.
	 */
	public File dump(Node node) {
		if (node == null) {
			return null;
		}
		try {
			Node nodeCopy = node.copy();
			nodeCopy.setParent(null);
			String filename = (Validations.isEmpty(nodeCopy.getId()) ? nodeCopy
					.getType() : nodeCopy.getId()) + ".xml";
			File dumpFile = new File(Files.createTempDirectory(), filename);
			Writer writer = Files.newWriter(dumpFile);
			toXML(nodeCopy, writer);
			writer.close();
			return dumpFile;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Restore node-graph from file dump.
	 */
	public Node restore(File dumpFile) {
		if (dumpFile == null) {
			return null;
		}
		try {
			Reader reader = Files.newReader(dumpFile);
			Node node = fromXML(reader);
			node.setId(Filenames.asId(Filenames.asTitle(dumpFile.getName())));
			reader.close();
			return node;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
