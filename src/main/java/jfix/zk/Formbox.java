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
import org.zkoss.zul.Textbox;
import org.zkoss.zul.impl.InputElement;

public class Formbox extends Grid {

	public Formbox() {
		setStyle("border: none;");
		setOddRowSclass("none");
	}

	public void add(String name) {
		add(name, null, null);
	}

	public void add(String name, Component component) {
		add(name, component, null);
	}

	public void add(Component component) {
		add(null, component, null);
	}

	public void add(String name, Component component, String description) {
		org.zkoss.zul.Row row = new org.zkoss.zul.Row();
		if (description != null) {
			row.setTooltiptext(description);
		}

		if (name != null) {
			Label label = newLabel(name, component);
			if (component == null) {
				label.setStyle("font-weight: bold");
			}
			Cell cell = new Cell(label);
			cell.setHflex("1");
			if (component == null) {
				cell.setColspan(2);
			}
			row.appendChild(cell);
		}

		if (component != null) {
			for (Component child : ZK.getDescendants(component)) {
				if (child instanceof Textbox) {
					((InputElement) child).setHflex("1");
				}
			}
			Cell cell = new Cell(component);
			cell.setHflex("6");
			if (name == null) {
				cell.setColspan(2);
			}
			row.appendChild(cell);
		}

		getRows().appendChild(row);
	}

	public boolean contains(Component comp) {
		return ZK.getDescendants(this).contains(comp);
	}

	public void remove(Component comp) {
		if (contains(comp)) {
			comp.getParent().getParent().setParent(null);
			comp.getParent().setParent(null);
			comp.setParent(null);
		}
	}

	public void setLabel(String name, Component comp) {
		((Label) comp.getParent().getParent().getFirstChild().getFirstChild())
				.setValue(name);
	}

	protected Label newLabel(String name, Component component) {
		return new Label(name);
	}
}
