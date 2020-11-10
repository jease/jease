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
package jease.cmf.web.node.tree;

import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeDesktop;
import jease.cmf.web.node.tree.container.ContainerTable;
import jease.cmf.web.node.tree.navigation.NavigationTree;
import jfix.zk.ActionListener;
import jfix.zk.Panel;

import org.zkoss.zk.ui.event.Event;

public class TreeDesktop extends NodeDesktop {

	private Panel navigationPanel;
	private NavigationTree navigationTree;

	public TreeDesktop() {
		appendNavigation();
		appendDesktop(new ContainerTable());
	}

	private void appendNavigation() {
		navigationTree = new NavigationTree();
		navigationTree.addSelectListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				refresh();
			}
		});
		navigationTree.addChangeListener(new ActionListener() {
			public void actionPerformed(Event event) {
				forceRefresh();
			}
		});
		navigationPanel = new Panel(navigationTree);
		navigationPanel.setHflex("3");
		appendChild(navigationPanel);
	}

	protected void refreshDesktop() {
		super.refreshDesktop();
		navigationPanel.clearToolbar();
		navigationPanel.appendChildToToolbar(newEditButton(JeaseSession
				.getContainer()));
		navigationTree.resetSelectedValue();
		navigationTree.refresh();
	}

}
