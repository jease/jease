/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeTable;
import jease.cmf.web.node.NodeTableModel;
import jfix.util.Arrays;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Modal;
import jfix.zk.Radiobutton;
import jfix.zk.Radiogroup;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

public class ContainerTable extends NodeTable {

	private NodeTableModel nodeTableModel;

	private Radiobutton singleSelect = new Radiobutton(I18N.get("Edit"));
	private Radiobutton multiSelect = new Radiobutton(I18N.get("Select"));

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
		nodeTableModel = JeaseSession.getConfig().newTableModel();
		init(new ContainerTableModel(nodeTableModel));
	}

	private void initItemRenderer() {
		ActionListener dropListener = new ActionListener() {
			public void actionPerformed(Event event) {
				dropPerformed((DropEvent) event);
			}
		};
		ListitemRenderer treeTableRenderer = new ContainerTableRenderer(
				getListbox().getItemRenderer(), dropListener);
		getListbox().setItemRenderer(treeTableRenderer);
	}

	private void initModeControl() {
		singleSelect.setChecked(true);
		singleSelect.addCheckListener(new ActionListener() {
			public void actionPerformed(Event event) {
				getListbox().setPageSize(getListbox().getPageSize() / 5);
				getListbox().setMultiple(false);
				getListbox().setCheckmark(false);
			}
		});
		multiSelect.addCheckListener(new ActionListener() {
			public void actionPerformed(Event event) {
				getListbox().setPageSize(getListbox().getPageSize() * 5);
				getListbox().setMultiple(true);
				getListbox().setCheckmark(true);
			}
		});
		appendChild(new Radiogroup(singleSelect, multiSelect));
	}

	private void dropPerformed(DropEvent dropEvent) {
		getListbox().renderAll();

		Component target = dropEvent.getTarget();
		Component dragged = dropEvent.getDragged();

		if (dragged instanceof Listitem) {
			Set<Listitem> itemSet = new HashSet<Listitem>(getListbox()
					.getSelectedItems());
			itemSet.add((Listitem) dragged);
			List<Listitem> itemList = new ArrayList(getListbox().getItems());
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
			Treeitem treeitem = (Treeitem) ((Treerow) dragged).getParent();
			Listitem listitem = new Listitem("");
			listitem.setValue(treeitem.getValue());
			dragged.getParent().removeChild(dragged);
			target.getParent().insertBefore(listitem, target);
		}

		try {
			Nodes.append(JeaseSession.getContainer(),
					Arrays.cast(getListbox().getValues(), Node.class));
		} catch (NodeException e) {
			Modal.error(e.getMessage());
		} finally {
			fireChangeEvent();
		}
	}

}
