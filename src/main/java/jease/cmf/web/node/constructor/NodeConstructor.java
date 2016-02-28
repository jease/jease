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
package jease.cmf.web.node.constructor;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.web.JeaseSession;
import jfix.zk.Refreshable;
import jfix.zk.Selectfield;

import org.apache.commons.lang3.ArrayUtils;

public class NodeConstructor extends Selectfield implements Refreshable {

	public NodeConstructor() {
		setItemRenderer(new NodeConstructorRenderer());
		setValues(ArrayUtils.add(JeaseSession.getConfig().newNodes(), 0, null));
	}

	public Node getSelectedNode() {
		Node node = (Node) getSelectedValue();
		if (node != null) {
			return node.copy(false);
		} else {
			return null;
		}
	}

	public void refresh() {
		int selectedIndex = getSelectedIndex();
		setSelectedIndex(0);
		for (int i = 1; i < getModel().getSize(); i++) {
			boolean valid = isValidChild((Node) getModel().getElementAt(i));
			getItemAtIndex(i).setDisabled(!valid);
			if (valid && i == selectedIndex) {
				getItemAtIndex(i).setSelected(true);
			}
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
