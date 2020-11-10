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
package jease.cmf.web.node.tree.navigation;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jfix.zk.ActionListener;
import jfix.zk.Modal;
import jfix.zk.Tree;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

public class NavigationTree extends Tree {

	public NavigationTree() {
		setTreeitemRenderer(new NavigationTreeRenderer(new ActionListener() {
			public void actionPerformed(Event event) {
				dropPerformed((DropEvent) event);
			}
		}));
		addSelectListener(new ActionListener() {
			public void actionPerformed(Event event) {
				selectPerformed();
			}
		});
		setModel(new NavigationTreeModel(JeaseSession.getRoots()));
	}

	public Object getSelectedValue() {
		Object value = super.getSelectedValue();
		if (value == null) {
			value = JeaseSession.getContainer();
		}
		return value;
	}

	private void selectPerformed() {
		Treeitem treeitem = getSelectedItem();
		if (treeitem != null) {
			Node value = (Node) treeitem.getValue();
			if (value != null) {
				JeaseSession.setContainer(value);
			}
		}
	}

	private void dropPerformed(DropEvent dropEvent) {
		Component target = dropEvent.getTarget();
		Component dragged = dropEvent.getDragged();
		Node draggedNode = null;
		if (dragged instanceof Listitem) {
			draggedNode = (Node) ((Listitem) dragged).getValue();
		}
		if (dragged instanceof Treerow) {
			draggedNode = (Node) ((Treeitem) ((Treerow) dragged).getParent())
					.getValue();
		}
		try {
			Node parentNode = (Node) ((Treeitem) ((Treerow) target).getParent())
					.getValue();
			Nodes.append(parentNode, draggedNode);
		} catch (NodeException e) {
			Modal.error(e.getMessage());
		} finally {
			fireChangeEvent();
		}
	}
}
