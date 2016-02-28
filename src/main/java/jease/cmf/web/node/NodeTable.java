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
package jease.cmf.web.node;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.constructor.NodeConstructor;
import jfix.zk.ObjectEditor;
import jfix.zk.ObjectTable;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;

public class NodeTable extends ObjectTable<Node> {

	private NodeConstructor nodeConstructor;

	public NodeTable() {
	}

	public void refresh() {
		if (nodeConstructor == null) {
			initNodeConstructor();
		}
		nodeConstructor.refresh();
		super.refresh();
	}

	private void initNodeConstructor() {
		nodeConstructor = new NodeConstructor();
		nodeConstructor.addEventListener(Events.ON_SELECT,
				$event -> onSelect(nodeConstructor.getSelectedNode()));
		getCreateButton().setVisible(true);
		getCreateButton().getParent().insertBefore(nodeConstructor,
				getCreateButton());
	}

	public void onSelect(Object obj) {
		if (obj == null) {
			obj = nodeConstructor.getSelectedNode();
		}
		if (obj instanceof Node) {
			final Node node = (Node) obj;
			ObjectEditor<? extends Node> nodeEditor = JeaseSession.getConfig()
					.newEditor(node);
			nodeEditor.addEventListener(
					Events.ON_CHANGE,
					$event -> {
						fireChangeEvent();
						int index = getElements().indexOf(node);
						if (index != -1) {
							getListbox().setActivePage(
									index / getListbox().getPageSize());
						}
					});
			setEditor(nodeEditor);
			super.onSelect(obj);
		}
	}

	protected void fireChangeEvent() {
		Events.sendEvent(new Event(Events.ON_CHANGE, this));
	}
}
