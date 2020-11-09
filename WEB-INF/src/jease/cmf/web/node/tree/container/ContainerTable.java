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
package jease.cmf.web.node.tree.container;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeTable;
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

	public ContainerTable() {
		initTableModel();
		initItemRenderer();
		initNodeConstructor();
	}

	private void initTableModel() {
		init(new ContainerTableModel());
	}

	private void initItemRenderer() {
		ActionListener dropListener = new ActionListener() {
			public void actionPerformed(Event event) {
				dropPerformed((DropEvent) event);
			}
		};
		ListitemRenderer treeTableRenderer = new ContainerTableRenderer(getListbox()
				.getItemRenderer(), dropListener);
		getListbox().setItemRenderer(treeTableRenderer);
		getListbox().setMold(null);
	}

	private void dropPerformed(DropEvent dropEvent) {
		getListbox().renderAll();

		Component target = dropEvent.getTarget();
		Component dragged = dropEvent.getDragged();

		if (dragged instanceof Listitem) {
			dragged.getParent().removeChild(dragged);
			target.getParent().insertBefore(dragged, target);
		}

		if (dragged instanceof Treerow) {
			Treeitem treeitem = (Treeitem) ((Treerow) dragged).getParent();
			Listitem listitem = new Listitem("");
			listitem.setValue(treeitem.getValue());
			dragged.getParent().removeChild(dragged);
			target.getParent().insertBefore(listitem, target);
		}

		try {
			Nodes.append(JeaseSession.getContainer(), Arrays.cast(getListbox()
					.getValues(), Node.class));
		} catch (NodeException e) {
			Modal.error(e.getMessage());
		}

		fireChangeEvent();
	}
}
