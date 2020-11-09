/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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
package jease.cmf.web;

import jease.cmf.web.i18n.Strings;
import jease.cmf.web.node.flat.FlatDesktop;
import jease.cmf.web.node.tree.TreeDesktop;
import jfix.zk.ActionListener;
import jfix.zk.Div;
import jfix.zk.Radiobutton;
import jfix.zk.Radiogroup;
import jfix.zk.Refreshable;

import org.zkoss.zk.ui.event.Event;

public class Jease extends Div implements Refreshable {

	private Div desktopArea = new Div();
	private Radiobutton viewTreeDesktop = new Radiobutton(Strings.Tree);
	private Radiobutton viewFlatDesktop = new Radiobutton(Strings.Flat);
	private TreeDesktop treeDesktop = new TreeDesktop();
	private FlatDesktop flatDesktop = new FlatDesktop();

	public Jease() {
		ActionListener desktopSwitch = new ActionListener() {
			public void actionPerformed(Event event) {
				switchDesktop();
			}
		};
		viewFlatDesktop.addCheckListener(desktopSwitch);
		viewTreeDesktop.addCheckListener(desktopSwitch);
		viewTreeDesktop.setChecked(true);
		desktopArea.appendChild(treeDesktop);
		appendChild(desktopArea);
		appendChild(new Radiogroup(viewTreeDesktop, viewFlatDesktop));
		initStyle();
	}

	private void initStyle() {
		setWidth("100%");
		desktopArea.setWidth("100%");
		treeDesktop.setWidth("100%");
		flatDesktop.setWidth("100%");
	}

	private void switchDesktop() {
		desktopArea.getChildren().clear();
		desktopArea.appendChild(viewTreeDesktop.isChecked() ? treeDesktop
				: flatDesktop);
		refresh();
	}

	public void refresh() {
		treeDesktop.refresh();
		flatDesktop.refresh();
	}
}
