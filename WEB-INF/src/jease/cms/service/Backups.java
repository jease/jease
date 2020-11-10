/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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
package jease.cms.service;

import java.io.File;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Backup;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jfix.functor.Procedure;

/**
 * Service to dump/restore contents into/from XML-files.
 */
public class Backups {

	private final static Backup backup = new Backup(
			Contents.getAvailableTypes());

	/**
	 * Dump given node into XML-File.
	 */
	public static File dump(Node node) {
		return backup.dump(node);
	}

	/**
	 * Append content contained in previously backuped XML-file to given
	 * container and assign ownership to given user.
	 */
	public static void restore(File backupFile, Node container, final User user)
			throws NodeException {
		Node node = backup.restore(backupFile);
		node.traverse(new Procedure<Node>() {
			public void execute(Node node) {
				((Content) node).setEditor(user);
			}
		});
		Nodes.append(container, node);
	}
}
