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
package jease.cmf.web.node.tree.container;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeTable;
import jease.cmf.web.node.NodeTableModel;
import jfix.util.Arrays;
import jfix.zk.ActionListener;
import jfix.zk.Modal;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.Treerow;

public class ContainerTable extends NodeTable {

	private ContainerClipboard clipboard;
	private NodeTableModel nodeTableModel;

	public ContainerTable() {
		initTableModel();
		initItemRenderer();
		initClipboard();
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

	private void initClipboard() {
		clipboard = new ContainerClipboard(nodeTableModel.getProportions());
		clipboard.setItemRenderer(getListbox().getItemRenderer());
		appendChild(clipboard);
	}

	private void dropPerformed(DropEvent dropEvent) {
		getListbox().renderAll();

		Component target = dropEvent.getTarget();
		Component dragged = dropEvent.getDragged();

		boolean targetIsNodeContainer = target.getParent() == getListbox();

		if (dragged instanceof Listitem) {
			dragged.getParent().removeChild(dragged);
			if (targetIsNodeContainer) {
				target.getParent().insertBefore(dragged, target);
			} else {
				// Item is dragged onto clipboard, so we disable further
				// dropping, because we want only one item in clipboard.
				clipboard.clip(((Listitem) dragged).getValue());
			}
		}

		if (dragged instanceof Treerow) {
			if (targetIsNodeContainer) {
				Treeitem treeitem = (Treeitem) ((Treerow) dragged).getParent();
				Listitem listitem = new Listitem("");
				listitem.setValue(treeitem.getValue());
				dragged.getParent().removeChild(dragged);
				target.getParent().insertBefore(listitem, target);
			} else {
				Treeitem treeitem = (Treeitem) ((Treerow) dragged).getParent();
				clipboard.clip(treeitem.getValue());
				dragged.getParent().removeChild(dragged);
			}
		}

		if (targetIsNodeContainer) {
			try {
				Nodes.append(JeaseSession.getContainer(), Arrays.cast(
						getListbox().getValues(), Node.class));
			} catch (NodeException e) {
				Modal.error(e.getMessage());
			} finally {
				fireChangeEvent();
			}
		}
	}

	public void refresh() {
		super.refresh();
		if (clipboard != null) {
			clipboard.clear();
		}
	}
}
