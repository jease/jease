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
package jease.cmf.web.node.constructor;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.web.JeaseSession;
import jfix.zk.Refreshable;
import jfix.zk.Selectfield;

public class NodeConstructor extends Selectfield implements Refreshable {

	public NodeConstructor() {
		setWidth(null);
		setItemRenderer(new NodeConstructorRenderer());
		setValues(JeaseSession.getConfig().newNodes());
	}

	public Node getSelectedNode() {
		Node node = (Node) getSelectedValue();
		if (node != null) {
			return node.copy();
		} else {
			return null;
		}
	}

	public void refresh() {
		Node currentSelection = (Node) getSelectedValue();
		for (int i = 1; i < getModel().getSize(); i++) {
			Node node = (Node) getModel().getElementAt(i);
			getItemAtIndex(i).setSelected(node == currentSelection);
			getItemAtIndex(i).setDisabled(!isValidChild(node));
		}
	}

	private boolean isValidChild(Node node) {
		try {
			JeaseSession.getContainer().validateChild(node, null);
			return true;
		} catch (NodeException e) {
			return false;
		}
	}
}