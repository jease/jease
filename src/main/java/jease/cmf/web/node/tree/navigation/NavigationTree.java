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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jfix.zk.Modal;
import jfix.zk.Tree;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;
import org.zkoss.zul.ext.TreeOpenableModel;

public class NavigationTree extends Tree {

	public NavigationTree() {
		setItemRenderer(new NavigationTreeRenderer(
				$event -> dropPerformed((DropEvent) $event)));
		addEventListener(Events.ON_SELECT, $event -> selectPerformed());
		setModel(new NavigationTreeModel(JeaseSession.getRoots(),
				JeaseSession.getFilter()));
		((TreeOpenableModel) getModel()).addOpenPath(new int[] { 0 });
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
		Node[] draggedNodes = null;
		if (dragged instanceof Listitem) {
			Listbox listbox = ((Listitem) dragged).getListbox();
			Set<Listitem> itemSet = new HashSet<>(listbox.getSelectedItems());
			itemSet.add((Listitem) dragged);
			List<Node> nodes = new ArrayList<>();
			for (Object item : listbox.getItems()) {
				if (itemSet.contains(item)) {
					if (((Listitem) item).getValue() instanceof Node) {
						nodes.add((Node) ((Listitem) item).getValue());
					}
				}
			}
			listbox.clearSelection();
			draggedNodes = nodes.toArray(new Node[] {});
		}
		if (dragged instanceof Treerow) {
			draggedNodes = new Node[] { (Node) ((Treeitem) ((Treerow) dragged)
					.getParent()).getValue() };
		}
		try {
			Node parentNode = (Node) ((Treeitem) ((Treerow) target).getParent())
					.getValue();
			for (Node node : draggedNodes) {
				Nodes.append(parentNode, node);
			}
		} catch (NodeException e) {
			Modal.error(e.getMessage());
		} finally {
			fireChangeEvent();
		}
	}

}
