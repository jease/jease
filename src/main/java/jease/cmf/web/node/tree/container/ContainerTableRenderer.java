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
package jease.cmf.web.node.tree.container;

import jease.cmf.domain.Node;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ContainerTableRenderer implements ListitemRenderer<Node> {

    private ListitemRenderer<Node> itemRenderer;
    private EventListener<Event> dropListener;

    public ContainerTableRenderer(ListitemRenderer<Node> itemRenderer,
            EventListener<Event> dropListener) {
        this.itemRenderer = itemRenderer;
        this.dropListener = dropListener;
    }

    public void render(Listitem listitem, Node value, int index)
            throws Exception {
        itemRenderer.render(listitem, value, index);
        if (value != null) {
            listitem.setDraggable(Node.class.getSimpleName());
        }
        listitem.setDroppable(Node.class.getSimpleName());
        listitem.addEventListener(Events.ON_DROP, dropListener);
    }

}
