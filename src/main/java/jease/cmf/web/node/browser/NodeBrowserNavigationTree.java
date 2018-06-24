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
package jease.cmf.web.node.browser;

import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.tree.navigation.NavigationTreeModel;
import jease.cmf.web.node.tree.navigation.NavigationTreeRenderer;
import jfix.zk.Tree;

import org.zkoss.zul.ext.TreeOpenableModel;

public class NodeBrowserNavigationTree extends Tree {

	public NodeBrowserNavigationTree() {
		setWidth("300px");
		setModel(new NavigationTreeModel(JeaseSession.getRoots(),
				JeaseSession.getFilter()));
		((TreeOpenableModel) getModel()).addOpenPath(new int[] { 0 });
		setItemRenderer(new NavigationTreeRenderer());
		refresh();
	}

	public Object getSelectedValue() {
		Object value = super.getSelectedValue();
		if (value == null) {
			value = JeaseSession.getContainer();
		}
		return value;
	}
}
