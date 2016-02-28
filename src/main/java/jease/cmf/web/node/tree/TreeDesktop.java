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
package jease.cmf.web.node.tree;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeDesktop;
import jease.cmf.web.node.tree.container.ContainerTable;
import jease.cmf.web.node.tree.navigation.NavigationTree;
import jease.cmf.web.node.tree.navigation.NavigationTreeModel;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Panel;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class TreeDesktop extends NodeDesktop {

	private Panel navigationPanel;
	private NavigationTree navigationTree;

	public TreeDesktop() {
		appendNavigation();
		appendDesktop(new ContainerTable());
	}

	private void appendNavigation() {
		navigationTree = new NavigationTree();
		navigationTree.addEventListener(Events.ON_SELECT, $event -> refresh());
		navigationTree.addEventListener(Events.ON_CHANGE,
				$event -> forceRefresh());
		navigationPanel = new Panel(navigationTree);
		navigationPanel.setHflex("3");
		appendChild(navigationPanel);
	}

	protected void refreshDesktop() {
		super.refreshDesktop();
		navigationPanel.clearToolbar();
		navigationPanel.appendChildToToolbar(newGoHomeButton());
		navigationPanel.appendChildToToolbar(newGoIntoButton());
		navigationTree.resetSelectedValue();
		navigationTree.refresh();
	}

	private Button newGoHomeButton() {
		Button button = new Toolbarbutton(null, Images.GoHome);
		button.setTooltiptext(I18N.get("Go_Home"));
		button.addEventListener(Events.ON_CLICK,
				$event -> refreshModel(JeaseSession.getRoots()));
		return button;
	}

	private Button newGoIntoButton() {
		Button button = new Toolbarbutton(null, Images.GoJump);
		button.setTooltiptext(I18N.get("Go_Into"));
		button.addEventListener(
				Events.ON_CLICK,
				$event -> refreshModel(new Node[] { JeaseSession.getContainer() }));
		return button;
	}

	private void refreshModel(Node[] roots) {
		navigationTree.setModel(null);
		navigationTree.setModel(new NavigationTreeModel(roots, JeaseSession
				.getFilter()));
		refreshDesktop();
	}

}
