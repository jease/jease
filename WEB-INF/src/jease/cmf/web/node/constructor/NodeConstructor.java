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
package jease.cmf.web.node.constructor;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jfix.zk.Refreshable;
import jfix.zk.Selectfield;

public class NodeConstructor extends Selectfield implements Refreshable {

	public NodeConstructor() {
		setWidth(null);
		setItemRenderer(new NodeConstructorRenderer());
	}

	public Node getSelectedNode() {
		return (Node) getSelectedValue();
	}

	public void refresh() {
		Node currentSelection = getSelectedNode();
		setValues(JeaseSession.getContainer().filterValidChildren(
				JeaseSession.getConfig().newNodes()));
		if (currentSelection != null) {
			for (int i = 1; i < getModel().getSize(); i++) {
				if (((Node) getModel().getElementAt(i)).getType().equals(
						currentSelection.getType())) {
					setSelectedIndex(i);
					break;
				}
			}
		}
	}

}
