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
package jease.cmf.web.node;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jfix.util.Validations;
import jfix.zk.ActionListener;
import jfix.zk.Linkbutton;
import jfix.zk.Panel;
import jfix.zk.Refreshable;
import jfix.zk.Row;

import org.zkoss.zk.ui.event.Event;

public abstract class NodeDesktop extends Row implements Refreshable {

	private Panel desktopPanel;
	private NodeTable desktopTable;
	private NodeRefreshState refreshState;

	public NodeDesktop() {
		setWidth("100%");
		setPack("stretch");
		refreshState = new NodeRefreshState();
	}

	protected void appendDesktop(NodeTable nodeTable) {
		desktopTable = nodeTable;
		desktopTable.addChangeListener(new ActionListener() {
			public void actionPerformed(Event event) {
				forceRefresh();
			}
		});
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

	protected Linkbutton newNavigationButton(final Node node) {
		Linkbutton button = new Linkbutton(node.getId() + " /");
		button.setDisabled(!node.isDescendant(JeaseSession.getRoots()));
		button.setTooltiptext(node.getType());
		button.addClickListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				JeaseSession.setContainer(node);
				forceRefresh();
			}
		});
		return button;
	}

	protected Linkbutton newEditButton(final Node node) {
		String label = Validations.isEmpty(node.getId()) ? node.getPath()
				: node.getId();
		Linkbutton button = new Linkbutton(label);
		button.setTooltiptext(node.getType());
		button.addClickListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				desktopTable.onSelect(node);
			}
		});
		return button;
	}

	public NodeTable getNodeTable() {
		return desktopTable;
	}
	
}
