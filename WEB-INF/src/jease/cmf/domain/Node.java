/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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
package jease.cmf.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jfix.db4o.Persistent;
import jfix.functor.Functors;
import jfix.functor.Predicate;
import jfix.functor.Procedure;
import jfix.util.Arrays;
import jfix.util.Urls;

public class Node extends Persistent {

	private transient static Set<Node> changedNodes = new HashSet();
	private String id;
	private Node parent;
	private Node[] children = new Node[] {};

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node newParent) {
		if (newParent == null) {
			detachParent();
		} else if (parent != newParent) {
			newParent.appendChild(this);
		}
	}

	public Node[] getParents() {
		List<Node> parents = new ArrayList();
		Node parentNode = getParent();
		while (parentNode != null) {
			parents.add(parentNode);
			parentNode = parentNode.getParent();
		}
		Collections.reverse(parents);
		return parents.toArray(new Node[] {});
	}

	public <E extends Node> E[] getParents(Class<E> clazz) {
		return Arrays.filter(getParents(), clazz);
	}

	public Node[] getChildren() {
		return children;
	}

	public <E extends Node> E[] getChildren(Class<E> clazz) {
		return Arrays.filter(getChildren(), clazz);
	}

	public Node getChild(String path) {
		if (path == null) {
			return null;
		}
		if (path.equals("")) {
			return this;
		}
		if (!path.contains("/")) {
			for (Node child : getChildren()) {
				if (path.equals(child.getId())) {
					return child;
				}
			}
			return null;
		}
		Node node = this;
		if (path.startsWith("/")) {
			while (node.getParent() != null) {
				node = node.getParent();
			}
			path = path.replaceFirst(node.getPath(), "");
		}
		for (String id : path.split("/")) {
			if ("".equals(id)) {
				continue;
			}
			node = node.getChild(id);
			if (node == null) {
				return null;
			}
		}
		return node;
	}

	public void appendChild(Node child) {
		child.detachParent();
		child.parent = this;
		children = Arrays.append(children, child, Node.class);
		markChanged();
	}

	public void appendChildren(Node[] newChildren) {
		Set<Node> newChildrenSet = Arrays.asSet(newChildren);
		Set<Node> currentChildrenSet = Arrays.asSet(children);
		List<Node> result = new ArrayList();
		if (newChildrenSet.containsAll(currentChildrenSet)) {
			result.addAll(Arrays.asList(newChildren));
		} else {
			int newChildIndex = 0;
			for (Node child : children) {
				if (newChildrenSet.contains(child)) {
					result.add(newChildren[newChildIndex++]);
				} else {
					result.add(child);
				}
			}
			for (int i = newChildIndex; i < newChildren.length; i++) {
				result.add((Node) newChildren[i]);
			}
		}
		for (Node node : result) {
			appendChild(node);
		}
	}

	public void detach() {
		detachChildren();
		detachParent();
	}

	private void detachChildren() {
		for (Node child : children) {
			child.detach();
		}
	}

	private void detachParent() {
		if (parent != null && Arrays.contains(parent.children, this)) {
			parent.children = Arrays.remove(parent.children, this, Node.class);
			parent.markChanged();
		}
		parent = null;
		markChanged();
	}

	public String getType() {
		return getClass().getSimpleName();
	}

	public String getTitle() {
		return id != null ? id : "";
	}

	public boolean isContainer() {
		return true;
	}

	public String getPath() {
		if (getParent() == null) {
			return "/" + getId();
		}
		StringBuilder sb = new StringBuilder();
		for (Node node = this; node != null; node = node.getParent()) {
			if (!"".equals(node.getId())) {
				sb.insert(0, "/" + node.getId());
			}
		}
		return sb.toString();
	}

	public long getSize() {
		return id.length();
	}

	public Node copy() {
		try {
			Node node = getClass().newInstance();
			for (Node child : getChildren()) {
				node.appendChild(child.copy());
			}
			node.setId(getId());
			return node;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public Node[] filterValidChildren(Node[] potentialChildren) {
		return Functors.filter(potentialChildren, new Predicate<Node>() {
			public boolean test(Node potentialChild) {
				try {
					validateChild(potentialChild, potentialChild.getId());
					return true;
				} catch (NodeException e) {
					return false;
				}
			}
		});
	}

	public void validateChild(Node potentialChild, String potentialChildId)
			throws NodeException {
		validateId(potentialChild, potentialChildId);
		validateDuplicate(potentialChild, potentialChildId);
		validateNesting(potentialChild, potentialChildId);
	}

	protected void validateId(Node potentialChild, String potentialChildId)
			throws NodeException {
		if (!String.valueOf(potentialChildId).equals(
				Urls.encode(String.valueOf(potentialChildId)))) {
			throw new NodeException.IllegalId();
		}
	}

	protected void validateDuplicate(Node potentialChild,
			String potentialChildId) throws NodeException {
		for (Node actualChild : getChildren()) {
			if (actualChild.getId().equals(potentialChildId)
					&& actualChild != potentialChild) {
				throw new NodeException.IllegalDuplicate();
			}
		}
	}

	protected void validateNesting(Node potentialChild, String potentialChildId)
			throws NodeException {
		for (Node parentNode = this; parentNode != null; parentNode = parentNode
				.getParent()) {
			if (potentialChild == parentNode) {
				throw new NodeException.IllegalNesting();
			}
		}
	}

	public void traverse(Procedure<Node> action) {
		action.execute(this);
		for (Node child : getChildren()) {
			child.traverse(action);
		}
	}

	public void processChangedNodes(Procedure<Node> action) {
		synchronized (changedNodes) {
			for (Node node : changedNodes) {
				action.execute(node);
			}
			changedNodes.clear();
		}
	}

	public void markChanged() {
		synchronized (changedNodes) {
			changedNodes.add(this);
		}
	}

	public String toString() {
		return String.format("%s - %s - %s", getPath(), getTitle(), getType());
	}

}
