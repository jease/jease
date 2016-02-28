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
import org.zkoss.zul.Columns;
import org.zkoss.zul.Rows;

public class Grid extends org.zkoss.zul.Grid {

	public Grid() {
		super();
		setHflex("1");
		appendChild(new Rows());
	}

	public Grid(String... columns) {
		super();
		setHflex("1");
		Columns header = new Columns();
		for (String col : columns) {
			org.zkoss.zul.Column column = new org.zkoss.zul.Column(col);
			header.appendChild(column);
		}
		appendChild(header);
		appendChild(new Rows());
	}

	public void add(Component... comps) {
		org.zkoss.zul.Row row = new org.zkoss.zul.Row();
		for (Component comp : comps) {
			row.appendChild(comp);
		}
		getRows().appendChild(row);
	}

}
