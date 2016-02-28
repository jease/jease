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
package jfix.zk;

import org.zkoss.zul.Comboitem;
import org.zkoss.zul.ComboitemRenderer;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Treeitem;
import org.zkoss.zul.TreeitemRenderer;

public abstract class ItemRenderer implements ListitemRenderer<Object>,
		TreeitemRenderer<Object>, ComboitemRenderer<Object> {

	public abstract String render(Object value);

	public void render(Listitem listitem, Object value, int index) {
		listitem.setValue(value);
		listitem.setLabel(render(value));
	}

	public void render(Treeitem treeitem, Object value, int index) {
		treeitem.setValue(value);
		treeitem.setLabel(render(value));
	}

	public void render(Comboitem comboitem, Object value, int index) {
		comboitem.setValue(value);
		comboitem.setLabel(render(value));
	}
}
