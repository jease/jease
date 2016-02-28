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
import org.zkoss.zul.Panelchildren;
import org.zkoss.zul.Toolbar;

public class Panel extends org.zkoss.zul.Panel {

	public Panel(Component... comps) {
		this("", comps);
	}

	public Panel(String title, Component... comps) {
		setTitle(title);
		appendChild(new Toolbar());
		Panelchildren panelchildren = new Panelchildren();
		panelchildren.setStyle("padding: 5px;");
		for (Component comp : comps) {
			panelchildren.appendChild(comp);
		}
		appendChild(panelchildren);
	}

	public void clearToolbar() {
		getTopToolbar().getChildren().clear();
	}

	public void appendChildToToolbar(Component child) {
		getTopToolbar().appendChild(child);
	}
}
