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
package jease.cmf.domain;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Stream;

import jfix.db4o.Persistent;
import jfix.util.Urls;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * A Node is the fundamental base for building tree-like content repositories. A
 * Node has an id, a reference to its parent node and contains an array of
 * children. The id must be unique between all the children of a node.
 * <p>
 * The Node-Class contains a transient static set of changed nodes which is used
 * to store references to all nodes which were changed during a reorganisation
 * of the tree (e.g. appending a node to another parent). This way the
 * persistence layer can perform updates to database very efficiently, because
 * it needs only to iterate the changed nodes and save them.
 */
public class Node extends Persistent {

    private transient static final Set<Node> changedNodes = new HashSet<>();
    private String id;
    private Node parent;
    private Node[] children = new Node[]{};

    /**
     * Returns the id of the node. If the id is null, an empty string is
     * returned.
     */
    public String getId() {
        return id != null ? id : "";
    }

    /**
     * Sets the id of the node. No checks are performed.
     */
    public void setId(final String id) {
        String oldPath = null;
        if (getParent() != null && StringUtils.isNotBlank(getId())) {
            oldPath = getPath();
        }
        this.id = id;
        if (StringUtils.isNotBlank(oldPath)) {
            onPathChange(oldPath);
        }
    }

    /**
     * Returns the parent of the node.
     */
    public Node getParent() {
        return parent;
    }

    /**
     * Sets given parent for node. Internally the call is forwarded to
     * #appendChild(Node).
     */
    public void setParent(final Node newParent) {
        if (newParent == null) {
            detachParent();
        } else if (parent != newParent) {
            newParent.appendChild(this);
        }
    }

    /**
     * Returns all parents of node ordered from root to parent of node.
     */
    public Node[] getParents() {
        List<Node> parents = new ArrayList<>();
        Node parentNode = getParent();
        while (parentNode != null) {
            parents.add(parentNode);
            parentNode = parentNode.getParent();
        }
        Collections.reverse(parents);
        return parents.toArray(new Node[parents.size()]);
    }

    /**
     * Returns all parents of node which are of given class type ordered from
     * root to parent of node.
     */
    public <E extends Node> E[] getParents(final Class<E> clazz) {
        return (E[]) Stream.of(getParents())
                .filter($node -> clazz.isAssignableFrom($node.getClass()))
                .toArray($size -> (E[]) Array.newInstance(clazz, $size));
    }

    /**
     * Returns true if node is a descendant of given parents.
     */
    public boolean isDescendant(final Node... possibleParents) {
        for (Node possibleParent : possibleParents) {
            if (this == possibleParent) {
                return true;
            }
            Node parentNode = getParent();
            while (parentNode != null) {
                if (parentNode == possibleParent) {
                    return true;
                }
                parentNode = parentNode.getParent();
            }
        }
        return false;
    }

    /**
     * Returns all descendant nodes by recursively traversing children.
     */
    public Node[] getDescendants() {
        final List<Node> nodes = new ArrayList<>();
        traverse(nodes::add);
        return nodes.toArray(new Node[nodes.size()]);
    }

    /**
     * Returns all descendant nodes of given class type by recursively
     * traversing children.
     */
    public <E extends Node> E[] getDescendants(final Class<E> clazz) {
        return (E[]) Stream.of(getDescendants())
                .filter($node -> clazz.isAssignableFrom($node.getClass()))
                .toArray($size -> (E[]) Array.newInstance(clazz, $size));
    }

    /**
     * Returns all children of the node.
     */
    public Node[] getChildren() {
        return children;
    }

    /**
     * Returns all children of given class type.
     */
    public <E extends Node> E[] getChildren(final Class<E> clazz) {
        return (E[]) Stream.of(getChildren())
                .filter(node -> clazz.isAssignableFrom(node.getClass()))
                .toArray(size -> (E[]) Array.newInstance(clazz, size));
    }

    /**
     * Returns a child by given path.
     */
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
            if (".".equals(id)) {
                Node parentNode = node.isContainer() ? node : node.getParent();
                node = parentNode != null ? parentNode : node;
                continue;
            }
            if ("..".equals(id)) {
                Node parentNode = node.isContainer() ? node.getParent() : node
                        .getParent().getParent();
                node = parentNode != null ? parentNode : node;
                continue;
            }
            node = node.getChild(id);
            if (node == null) {
                return null;
            }
        }
        return node;
    }

    /**
     * Appends given child to node. This method automatically detaches the given
     * child before the child is attached to the new parent.
     */
    public void appendChild(final Node child) {
        String oldPath = null;
        if (child.getParent() != null && StringUtils.isNotBlank(child.getId())) {
            oldPath = child.getPath();
        }
        child.detachParent();
        child.parent = this;
        children = ArrayUtils.add(children, child);
        markChanged();
        if (StringUtils.isNotBlank(oldPath)) {
            child.onPathChange(oldPath);
        }
    }

    /**
     * Appens given children to node. This method automatically detaches all
     * children before each child is attached to the new parent.
     */
    public void appendChildren(final Node[] newChildren) {
        Set<Node> newChildrenSet = new HashSet<>(Arrays.asList(newChildren));
        Set<Node> currentChildrenSet = new HashSet<>(Arrays.asList(children));
        List<Node> result = new ArrayList<>();
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
                result.add(newChildren[i]);
            }
        }
        result.forEach(this::appendChild);
    }

    /**
     * Detaches a node from node-tree by detaching parent and all children.
     */
    public void detach() {
        detachChildren();
        detachParent();
    }

    /**
     * Detaches all children from node.
     */
    protected void detachChildren() {
        for (Node child : children) {
            child.detach();
        }
    }

    /**
     * Detaches parent from node.
     */
    protected void detachParent() {
        if (parent != null && ArrayUtils.contains(parent.children, this)) {
            parent.children = ArrayUtils.removeElement(parent.children, this);
            parent.markChanged();
        }
        parent = null;
        markChanged();
    }

    /**
     * Returns type of node as string. Per default the type is the simple class
     * name.
     */
    public String getType() {
        return getClass().getSimpleName();
    }

    /**
     * Returns true if node accepts children.
     */
    public boolean isContainer() {
        return true;
    }

    /**
     * Returns the path for the node. The path is built from the root node to
     * the current node by joining the ids with slashes (/).
     */
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

    /**
     * Returns the estimated size of the node in bytes.
     */
    public long getSize() {
        return getId().length();
    }

    /**
     * Creates a copy of the node. This method should be overriden by derived
     * classes by calling #super.copy() and then copying all class-specific
     * fields to copy.
     * <p>
     * If recursive is true, a recursive copy is performed where children and
     * children of children will be copied as well.
     */
    public Node copy(final boolean recursive) {
        try {
            Node node = getClass().newInstance();
            if (recursive) {
                for (Node child : getChildren()) {
                    node.appendChild(child.copy(recursive));
                }
            }
            node.setId(getId());
            return node;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * Validates if the given child with given id can be appended to node.
     */
    public void validateChild(final Node potentialChild,
                              final String potentialChildId) throws NodeException {
        validateId(potentialChild, potentialChildId);
        validateDuplicate(potentialChild, potentialChildId);
        validateNesting(potentialChild, potentialChildId);
        potentialChild.validateParent(this, potentialChildId);
    }

    /**
     * Valididates if given id for given child is correct.
     */
    protected void validateId(final Node potentialChild,
                              final String potentialChildId) throws NodeException {
        if (potentialChildId != null && !Urls.isValid(potentialChildId)) {
            throw new NodeException.IllegalId();
        }
    }

    /**
     * Validates if given child with given id is unique between children of a
     * node.
     */
    protected void validateDuplicate(final Node potentialChild,
                                     final String potentialChildId) throws NodeException {
        for (Node actualChild : getChildren()) {
            if (actualChild.getId().equals(potentialChildId)
                    && actualChild != potentialChild) {
                throw new NodeException.IllegalDuplicate();
            }
        }
    }

    /**
     * Validates if given child with given id can be child of node. Use this
     * method in derived implementations to restrict the set of valid children.
     */
    protected void validateNesting(final Node potentialChild,
                                   final String potentialChildId) throws NodeException {
        for (Node parentNode = this; parentNode != null; parentNode = parentNode
                .getParent()) {
            if (potentialChild == parentNode) {
                throw new NodeException.IllegalNesting();
            }
        }
    }

    /**
     * Validates if node with given potential id can be attached to given
     * potential parent. Use this method in derived implementations to restrict
     * the set of valid parents for certain kinds of nodes.
     */
    protected void validateParent(final Node potentialParent,
                                  final String potentialId) throws NodeException {
        // No restrictions per default.
    }

    /**
     * Applies given procedure to node and recursively to all children.
     */
    public void traverse(final Consumer<Node> action) {
        action.accept(this);
        for (Node child : getChildren()) {
            child.traverse(action);
        }
    }

    /**
     * Applies given procedure to all changed nodes.
     */
    public void processChangedNodes(final Consumer<Node> action) {
        synchronized (changedNodes) {
            changedNodes.forEach(action::accept);
            changedNodes.clear();
        }
    }

    /**
     * Marks the node as changed. Usually you don't have to call this method
     * directly.
     */
    public void markChanged() {
        synchronized (changedNodes) {
            changedNodes.add(this);
        }
    }

    /**
     * Gets called when the path is changed for current Node.
     */
    protected void onPathChange(String oldPath) {
    }

    public String toString() {
        return getPath();
    }

}
