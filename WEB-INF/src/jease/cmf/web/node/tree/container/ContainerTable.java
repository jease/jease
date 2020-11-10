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

import jease.cmf.domain.*;
import jease.cmf.service.*;
import jease.cmf.web.*;
import jease.cmf.web.node.*;
import jfix.util.*;
import jfix.zk.*;

import org.zkoss.zk.ui.*;
import org.zkoss.zk.ui.event.*;
import org.zkoss.zul.*;

public class ContainerTable extends NodeTable {

	private ContainerClipboard clipboard = new ContainerClipboard();

	public ContainerTable() {
		initTableModel();
		initItemRenderer();
		initClipboard();
	}

	private void initTableModel() {
		init(new ContainerTableModel(JeaseSession.getConfig().newTableModel()));
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
			target.getParent().insertBefore(dragged, target);
			if (!targetIsNodeContainer) {
				// Item is dragged onto clipboard, so we disable further
				// dropping, because we want only one item in clipboard.
				((Listitem) dragged).setDroppable(null);
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
		clipboard.clear();
	}
}
