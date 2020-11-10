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

import jfix.zk.Images;
import jfix.zk.Listbox;

import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ContainerClipboard extends Listbox {

	public ContainerClipboard(int[] props) {
		setWidth("100%");
		setNullable(false);
		setRows(1);
		Listhead listhead = new Listhead();
		listhead.setStyle("height: 1px;");
		for (int i = 0; i < props.length; i++) {
			Listheader listheader = new Listheader(" ");
			listheader.setHflex("" + props[i]);
			listhead.appendChild(listheader);
		}
		appendChild(listhead);
		setValues(new Object[] { null });
	}

	public void setItemRenderer(final ListitemRenderer itemRenderer) {
		super.setItemRenderer(new ListitemRenderer() {
			public void render(Listitem listitem, Object object)
					throws Exception {
				itemRenderer.render(listitem, object);
				if (object == null) {
					listitem.setImage(Images.Paste);
				}
			}
		});
	}

	public void clear() {
		setValues(new Object[] { null });
	}

	public void clip(Object object) {
		setValues(new Object[] { object });
		renderAll();
		getItemAtIndex(0).setDroppable(null);
	}
}
