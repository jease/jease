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

import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.tree.container.ContainerTable;
import jease.cmf.web.node.tree.navigation.NavigationTree;
import jfix.zk.ActionListener;
import jfix.zk.Panel;
import jfix.zk.Refreshable;
import jfix.zk.Row;

import org.zkoss.zk.ui.event.Event;

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
		appendChild(containerPanel);
	}

	private void initStyle() {
		navigationPanel.setWidth(NAVIGATION_WIDTH);
		containerPanel.setWidth("100%");
	}

	public void refresh() {
		containerTable.refresh();
		navigationTree.refresh();
		containerPanel.setTitle(JeaseSession.getContainer().getPath());		
		navigationPanel.setTitle(JeaseSession.getContainer().getTitle());
	}

}
