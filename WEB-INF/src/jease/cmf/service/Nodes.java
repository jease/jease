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

/**
 * Static utility to ease the persistence-handling of Nodes.
 * 
 * Each Jease-Repository has one special Node called "root". The root is the
 * only Node in a repository which doesn't have a parent node. All Nodes which
 * are not the root and don't have a parent, are deleted when a persistence
 * operation is performed.
 */
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

	/**
	 * Sets the root-node for a repository. This method should only be called
	 * once to initialize a repository.
	 */
	public static void setRoot(Node rootNode) {
		root = rootNode;
	}

	/**
	 * Returns the root-node of the repository. A root node is the only node in
	 * a repository which doesn't have a parent.
	 */
	public static Node getRoot() {
		return root;
	}

	/**
	 * Returns the system time (milliseconds) of the last change in database.
	 */
	public static long queryLastChange() {
		return Database.query(lastChange);
	}

	/**
	 * Appends given child to given node and saves changes to database.
	 * 
	 * Please note: appending a child to a node automatically removes the child
	 * from the former container.
	 */
	public static void append(Node node, Node child) throws NodeException {
		node.validateChild(child, child.getId());
		node.appendChild(child);
		Nodes.save(node);
	}

	/**
	 * Appends given children to given node and save changes to database.
	 * 
	 * Please note: appending a child to a node automatically removes the child
	 * from the former container.
	 */
	public static void append(Node node, Node[] children) throws NodeException {
		for (Node child : children) {
			node.validateChild(child, child.getId());
		}
		node.appendChildren(children);
		Nodes.save(node);
	}

	/**
	 * Saves all changes of a given node to database.
	 */
	public static void save(final Node node) {
		Database.write(new Command() {
			public void run() {
				node.markChanged();
				persistChanges();
			}
		});
	}

	/**
	 * Deletes given node from repository.
	 */
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
