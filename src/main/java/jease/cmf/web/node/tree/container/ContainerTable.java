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
package jease.cmf.web.node.tree.container;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeTable;
import jease.cmf.web.node.NodeTableModel;
import jfix.util.I18N;
import jfix.zk.Modal;
import jfix.zk.Radiogroup;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Radio;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

public class ContainerTable extends NodeTable {

	private Radio singleSelect = new Radio(I18N.get("Edit"));
	private Radio multiSelect = new Radio(I18N.get("Select"));

	public ContainerTable() {
		initTableModel();
		initItemRenderer();
		initModeControl();
	}

	public void onSelect(Object obj) {
		if (!getListbox().isMultiple()) {
			super.onSelect(obj);
		}
	}

	private void initTableModel() {
		init(new ContainerTableModel(JeaseSession.getConfig().newTableModel()));
	}

	private void initItemRenderer() {
		ListitemRenderer<Node> treeTableRenderer = new ContainerTableRenderer(
				getListbox().getItemRenderer(),
				$event -> dropPerformed((DropEvent) $event));
		getListbox().setItemRenderer(treeTableRenderer);
	}

	private void initModeControl() {
		singleSelect.setChecked(true);
		singleSelect.addEventListener(Events.ON_CHECK, $event -> {
			getListbox().setPageSize(getListbox().getPageSize() / 10);
			getListbox().setMultiple(false);
			getListbox().setCheckmark(false);
		});
		multiSelect.addEventListener(Events.ON_CHECK, $event -> {
			getListbox().setPageSize(getListbox().getPageSize() * 10);
			getListbox().setMultiple(true);
			getListbox().setCheckmark(true);
		});
		appendChild(new Radiogroup(singleSelect, multiSelect));
	}

	private void dropPerformed(DropEvent dropEvent) {
		getListbox().renderAll();

		Component target = dropEvent.getTarget();
		Component dragged = dropEvent.getDragged();

		if (dragged instanceof Listitem) {
			Set<Listitem> itemSet = new HashSet<>(getListbox()
					.getSelectedItems());
			itemSet.add((Listitem) dragged);
			List<Listitem> itemList = new ArrayList<>(getListbox().getItems());
			itemList.retainAll(itemSet);
			for (Listitem listitem : itemList) {
				if (listitem != target) {
					listitem.getParent().removeChild(listitem);
					target.getParent().insertBefore(listitem, target);
				}
			}
			getListbox().clearSelection();
		}

		if (dragged instanceof Treerow) {
			Treeitem treeitem = (Treeitem) dragged.getParent();
			Listitem listitem = new Listitem("");
			listitem.setValue(treeitem.getValue());
			dragged.getParent().removeChild(dragged);
			target.getParent().insertBefore(listitem, target);
		}

		try {
			Nodes.append(
					JeaseSession.getContainer(),
					Arrays.stream(getListbox().getValues()).toArray(
							$size -> (Node[]) Array.newInstance(Node.class,
									$size)));
		} catch (NodeException e) {
			Modal.error(e.getMessage());
		} finally {
			fireChangeEvent();
		}
	}
}
