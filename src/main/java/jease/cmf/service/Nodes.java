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

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jfix.db4o.Database;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Static utility to ease the persistence-handling of Nodes.
 * 
 * Each Jease-Repository has one special Node called "root". The root is the
 * only Node in a repository which doesn't have a parent node. All Nodes which
 * are not the root and don't have a parent, are deleted when a persistence
 * operation is performed.
 */
public class Nodes {

	private static Node root = queryRoot();

	private static Supplier<Map<String, Node>> nodesByPath = ConcurrentHashMap::new;

	/**
	 * Returns the root node derived by a database-query.
	 */
	public static Node queryRoot() {
		return Database.queryUnique(Node.class,
				$node -> $node.getParent() == null);
	}

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
		return Database.ext().getTimestamp();
	}

	/**
	 * Returns true if given node is root or attached to a parent.
	 */
	public static boolean isRooted(Node node) {
		return node == root || ArrayUtils.contains(node.getParents(), root);
	}

	/**
	 * Returns node from root by given path.
	 */
	public static Node getByPath(String path) {
		if (root == null) {
			return null;
		}
		Map<String, Node> cache = Database.query(nodesByPath);
		if (!cache.containsKey(path)) {
			Node node = root.getChild(path);
			if (node != null) {
				cache.put(path, node);
			}
		}
		return cache.get(path);
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
	public static void save(Node node) {
		Processor.save.accept(node);
	}

	/**
	 * Deletes given node from repository.
	 */
	public static void delete(Node node) {
		Processor.delete.accept(node);
	}

	/**
	 * Inner class to customize save & delete operations. Calls to save / delete
	 * are delegated to Processor. Set a customized processor for save / delete
	 * / traverse for logging, workflow, etc.pp.
	 */
	public static class Processor {

		private static Save save = new Save();
		private static Delete delete = new Delete();
		private static Traverse traverse = new Traverse();

		public static class Save implements Consumer<Node> {
			public void accept(final Node node) {
				Database.write(() -> {
					node.markChanged();
					root.processChangedNodes(traverse);
				});
			}
		}

		public static class Delete implements Consumer<Node> {
			public void accept(final Node node) {
				Database.write(() -> {
					node.detach();
					root.processChangedNodes(traverse);
				});
			}
		}

		public static class Traverse implements Consumer<Node> {
			public void accept(Node node) {
				if (isRooted(node)) {
					Database.save(node);
				} else {
					Database.ext().deleteDeliberately(node);
				}
			}
		}

		public static void set(Save processor) {
			save = processor;
		}

		public static void set(Delete processor) {
			delete = processor;
		}

		public static void set(Traverse processor) {
			traverse = processor;
		}

	}

}