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
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class Expander extends Column {

	private Component component;
	private Button button;

	public Expander(String caption, Component component, boolean open) {
		this.component = component;
		this.button = new Toolbarbutton(caption);
		this.button.addEventListener(Events.ON_CLICK, event -> toogle());
		appendChild(button);
		setOpen(open);
	}

	public void setOpen(boolean open) {
		if (open) {
			appendChild(component);
			button.setImage(Images.ExpandableOn);
		} else {
			removeChild(component);
			button.setImage(Images.ExpandableOff);
		}
	}

	public boolean isOpen() {
		return getChildren().contains(component);
	}

	public void toogle() {
		setOpen(!isOpen());
	}
}
