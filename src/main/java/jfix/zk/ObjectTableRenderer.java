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

import org.zkoss.zk.ui.Component;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;

public class ObjectTableRenderer<E> implements ListitemRenderer<E> {

	private ObjectTableModel<E> model;

	public ObjectTableRenderer(ObjectTableModel<E> model) {
		this.model = model;
	}

	public void render(final Listitem listitem, E obj, int index)
			throws Exception {
		listitem.setValue(obj);
		for (int i = 0; i < model.getColumns().length; i++) {
			E cellObj = (E) model.getCheckedValue(obj, i);
			if (cellObj instanceof View) {
				Listcell cell = new Listcell();
				cell.appendChild(((View<E>) cellObj).getComponent());
				listitem.appendChild(cell);
			} else if (cellObj instanceof Component) {
				Listcell cell = new Listcell();
				cell.appendChild((Component) cellObj);
				listitem.appendChild(cell);
			} else {
				Label label = new Label((String.valueOf(ObjectConverter
						.convert(cellObj))));
				label.setMultiline(true);
				Listcell cell = new Listcell();
				cell.appendChild(label);
				listitem.appendChild(cell);
			}
		}
	}

}
