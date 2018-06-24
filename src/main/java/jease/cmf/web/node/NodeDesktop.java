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
import jfix.zk.Panel;
import jfix.zk.Refreshable;
import jfix.zk.Row;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public abstract class NodeDesktop extends Row implements Refreshable {

	private Panel desktopPanel;
	private NodeTable desktopTable;
	private NodeRefreshState refreshState;

	public NodeDesktop() {
		refreshState = new NodeRefreshState();
	}

	protected void appendDesktop(NodeTable nodeTable) {
		desktopTable = nodeTable;
		desktopTable.addEventListener(Events.ON_CHANGE,
				$event -> forceRefresh());
		desktopPanel = new Panel(desktopTable);
		desktopPanel.setHflex("9");
		appendChild(desktopPanel);
	}

	public void refresh() {
		if (refreshState.isStale()) {
			refreshDesktop();
			desktopTable.refresh();
		}
	}

	protected void forceRefresh() {
		refreshState.reset();
		refresh();
	}

	protected void refreshDesktop() {
		Node node = JeaseSession.getContainer();
		desktopPanel.clearToolbar();
		for (Node parent : node.getParents()) {
			desktopPanel.appendChildToToolbar(newNavigationButton(parent));
		}
		desktopPanel.appendChildToToolbar(newEditButton(node));
	}

	protected Button newNavigationButton(final Node node) {
		Button button = new Toolbarbutton(node.getId() + " /");
		button.setDisabled(!node.isDescendant(JeaseSession.getRoots()));
		button.setTooltiptext(node.getType());
		button.addEventListener(Events.ON_CLICK, $event -> {
			JeaseSession.setContainer(node);
			forceRefresh();
		});
		return button;
	}

	protected Button newEditButton(final Node node) {
		String label = StringUtils.isBlank(node.getId()) ? node.getPath()
				: node.getId();
		Button button = new Toolbarbutton(label);
		button.setTooltiptext(node.getType());
		button.addEventListener(Events.ON_CLICK,
				$event -> desktopTable.onSelect(node));
		return button;
	}

	public NodeTable getNodeTable() {
		return desktopTable;
	}

}
