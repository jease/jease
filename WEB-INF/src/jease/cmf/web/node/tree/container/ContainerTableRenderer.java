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
package jease.cmf.web.node.tree.container;

import jease.cmf.domain.Node;
import jfix.zk.ActionListener;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ContainerTableRenderer implements ListitemRenderer {
	
	private ListitemRenderer itemRenderer;
	private ActionListener dropListener;
	
	public ContainerTableRenderer(ListitemRenderer itemRenderer, ActionListener dropListener) {
		this.itemRenderer = itemRenderer;
		this.dropListener = dropListener;
	}

	public void render(Listitem listitem, Object value) throws Exception {
		itemRenderer.render(listitem, value);
		if (value != null) {
			listitem.setDraggable(Node.class.getSimpleName());
		}
		listitem.setDroppable(Node.class.getSimpleName());
		listitem.addEventListener(Events.ON_DROP, dropListener);
	}

}
