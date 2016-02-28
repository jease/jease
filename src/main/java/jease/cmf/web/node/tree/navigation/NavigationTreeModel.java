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
package jease.cmf.web.node.tree.navigation;

import java.util.stream.Stream;

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeFilter;

import org.zkoss.zul.AbstractTreeModel;

public class NavigationTreeModel extends AbstractTreeModel<Object> {

	private NodeFilter nodeFilter;

	public NavigationTreeModel(Node[] roots, NodeFilter nodeFilter) {
		super(roots);
		this.nodeFilter = nodeFilter;
	}

	public Object getChild(Object node, int idx) {
		if (node instanceof Node[]) {
			return ((Node[]) node)[idx];
		} else {
			Node[] children = getChildContainer((Node) node);
			if (children.length > 0) {
				return children[Math.min(idx, children.length - 1)];
			} else {
				return null;
			}
		}
	}

	public int getChildCount(Object node) {
		if (node instanceof Node[]) {
			return ((Node[]) node).length;
		} else {
			return getChildContainer((Node) node).length;
		}
	}

	public boolean isLeaf(Object node) {
		if (node instanceof Node[]) {
			return false;
		} else {
			return getChildContainer((Node) node).length == 0;
		}
	}

	private Node[] getChildContainer(final Node node) {
		Node[] result = Stream.of(node.getChildren()).filter(Node::isContainer).toArray($size -> new Node[$size]);
		return result == null ? new Node[] {} : nodeFilter.apply(result);
	}
}
