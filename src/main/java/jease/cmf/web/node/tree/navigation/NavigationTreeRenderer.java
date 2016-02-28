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
package jease.cmf.web.node.tree.navigation;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;

import org.apache.commons.lang3.ArrayUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

public class NavigationTreeRenderer implements TreeitemRenderer<Node> {

	private EventListener<Event> dropListener;

	public NavigationTreeRenderer() {
	}

	public NavigationTreeRenderer(EventListener<Event> dropListener) {
		this.dropListener = dropListener;
	}

	public void render(Treeitem treeitem, Node node, int index)
			throws Exception {
		treeitem.setLabel(node.getId());
		treeitem.setTooltiptext(node.getType());
		treeitem.setImage(JeaseSession.getConfig().getIcon(node));
		treeitem.setValue(node);
		if (dropListener != null) {
			if (!ArrayUtils.contains(JeaseSession.getRoots(), node)) {
				treeitem.getTreerow().setDraggable(Node.class.getSimpleName());
			}
			treeitem.getTreerow().setDroppable(Node.class.getSimpleName());
			treeitem.getTreerow()
					.addEventListener(Events.ON_DROP, dropListener);
		}
	}
}
