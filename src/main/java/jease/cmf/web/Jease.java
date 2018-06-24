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
package jease.cmf.web;

import jease.cmf.web.node.NodeTable;
import jease.cmf.web.node.flat.FlatDesktop;
import jease.cmf.web.node.tree.TreeDesktop;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Radiogroup;
import jfix.zk.Refreshable;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Radio;

public class Jease extends Div implements Refreshable {

	private Div workspace = new Div();
	private Div accessory = new Div();
	private Radio viewTreeDesktop = new Radio(I18N.get("Navigation"));
	private Radio viewFlatDesktop = new Radio(I18N.get("Search"));
	private TreeDesktop treeDesktop = new TreeDesktop();
	private FlatDesktop flatDesktop = new FlatDesktop();

	public Jease() {
		setWidth("100%");

		workspace.setWidth("100%");
		workspace.appendChild(treeDesktop);

		accessory.setWidth("100%");
		accessory.appendChild(new Radiogroup(viewTreeDesktop, viewFlatDesktop));

		appendChild(workspace);
		appendChild(accessory);

		viewFlatDesktop.addEventListener(Events.ON_CHECK,
				$event -> switchDesktop());
		viewTreeDesktop.addEventListener(Events.ON_CHECK,
				$event -> switchDesktop());
		viewTreeDesktop.setChecked(true);
	}

	private void switchDesktop() {
		workspace.getChildren().clear();
		workspace.appendChild(viewTreeDesktop.isChecked() ? treeDesktop
				: flatDesktop);
		refresh();
	}

	public void refresh() {
		((Refreshable) workspace.getFirstChild()).refresh();
	}

	protected Component getWorkspace() {
		return workspace;
	}

	protected Component getAccessory() {
		return accessory;
	}

	protected NodeTable getTreeTable() {
		return treeDesktop.getNodeTable();
	}

	protected NodeTable getFlatTable() {
		return flatDesktop.getNodeTable();
	}

}
