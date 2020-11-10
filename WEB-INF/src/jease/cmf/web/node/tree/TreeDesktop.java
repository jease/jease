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
package jease.cmf.web.node.tree;

import jease.cmf.web.*;
import jease.cmf.web.node.tree.container.*;
import jease.cmf.web.node.tree.navigation.*;
import jfix.zk.*;

import org.zkoss.zk.ui.event.*;

public class TreeDesktop extends Row implements Refreshable {

	public static String NAVIGATION_WIDTH = "250px";

	private Panel navigationPanel;
	private Panel containerPanel;
	private NavigationTree navigationTree;
	private ContainerTable containerTable;

	public TreeDesktop() {
		initNavigationTree();
		initContainerTable();
		initPanels();
		initStyle();
		refresh();
	}

	private void initNavigationTree() {
		navigationTree = new NavigationTree();
		navigationTree.addSelectListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				refresh();
			}
		});
		navigationTree.addChangeListener(new ActionListener() {
			public void actionPerformed(Event event) {
				refresh();
			}
		});
	}

	private void initContainerTable() {
		containerTable = new ContainerTable();
		containerTable.addChangeListener(new ActionListener() {
			public void actionPerformed(Event event) {
				refresh();
			}
		});
	}

	private void initPanels() {
		navigationPanel = new Panel(navigationTree);
		containerPanel = new Panel(containerTable);
		appendChild(navigationPanel);
		appendChild(new Splitter(Splitter.COLLAPSE_BEFORE));
		appendChild(containerPanel);
	}

	private void initStyle() {
		setWidths(NAVIGATION_WIDTH);
	}

	public void refresh() {
		containerTable.refresh();
		navigationTree.refresh();
		containerPanel.setTitle(JeaseSession.getContainer().getPath());
		navigationPanel.setTitle(JeaseSession.getContainer().getTitle());
	}

}
