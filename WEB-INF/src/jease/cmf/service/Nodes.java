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
package jease.cmf.service;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jfix.db4o.Database;
import jfix.functor.Command;
import jfix.functor.Predicate;
import jfix.functor.Procedure;
import jfix.functor.Supplier;

public class Nodes {

	private static Node root = Database.queryUnique(Node.class,
			new Predicate<Node>() {
				public boolean test(Node node) {
					return node.getParent() == null;
				}
			});

	private static Supplier<Long> lastChange = new Supplier<Long>() {
		public Long get() {
			return System.currentTimeMillis();
		}
	};

	public static void setRoot(Node rootNode) {
		root = rootNode;
	}

	public static Node getRoot() {
		return root;
	}

	public static long queryLastChange() {
		return Database.query(lastChange);
	}

	public static void append(Node node, Node child) throws NodeException {
		node.validateChild(child, child.getId());
		node.appendChild(child);
		Nodes.save(node);
	}

	public static void append(Node node, Node[] children) throws NodeException {
		for (Node child : children) {
			node.validateChild(child, child.getId());
		}
		node.appendChildren(children);
		Nodes.save(node);
	}

	public static void save(final Node node) {
		Database.write(new Command() {
			public void run() {
				node.markChanged();
				persistChanges();
			}
		});
	}

	public static void delete(final Node node) {
		Database.write(new Command() {
			public void run() {
				node.detach();
				persistChanges();
			}
		});
	}

	private static void persistChanges() {
		root.processChangedNodes(new Procedure<Node>() {
			public void execute(Node node) {
				if (node == root || node.getParent() != null) {
					Database.save(node);
				} else {
					Database.ext().deleteDeliberately(node);
				}
			}
		});
	}
}
