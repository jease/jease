/*
    Copyright (C) 2013 maik.jablonski@jease.org

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

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeFilter;
import jfix.functor.Functors;
import jfix.functor.Predicate;

import org.zkoss.zul.AbstractTreeModel;

public class NavigationTreeModel extends AbstractTreeModel {

	private NodeFilter nodeFilter;
	
	public NavigationTreeModel(Node[] roots, NodeFilter nodeFilter) {
		super(roots);
		this.nodeFilter = nodeFilter;
	}

	public Object getChild(Object node, int idx) {
		if (node instanceof Node[]) {
			return ((Node[]) node)[idx];
		} else {
			return getChildContainer((Node) node)[idx];
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

	private Node[] getChildContainer(Node node) {
		Node[] result = Functors.filter(node.getChildren(),
				new Predicate<Node>() {
					public boolean test(Node obj) {
						return obj.isContainer();
					}
				});
		return result == null ? new Node[] {} : nodeFilter.apply(result);
	}
}
