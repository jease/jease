/*
    Copyright (C) 2013 maik.jablonski@jease.org

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
package jease.cms.web.component;

import jease.cmf.service.Nodes;
import jease.cmf.web.node.browser.NodeBrowserWindow;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Div;
import jfix.zk.Images;
import jfix.zk.Linkbutton;
import jfix.zk.Textfield;
import jfix.zk.WebBrowser;

import org.zkoss.zk.ui.event.Event;

public class Linkfield extends Div {

	Textfield url = new Textfield();
	Button browse = new Button();
	Linkbutton preview = new Linkbutton();

	public Linkfield() {
		browse.setTooltiptext(I18N.get("Browser"));
		browse.setImage(Images.UserHome);
		browse.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				browsePerformed();
			}
		});

		preview.setTooltiptext(I18N.get("Open"));
		preview.setImage(Images.InternetWebBrowser);
		preview.setWidth("24px");
		preview.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				if (!url.isEmpty()) {
					getRoot().appendChild(new WebBrowser(url.getText()));
				}
			}
		});

		appendChild(url);
		appendChild(browse);
		appendChild(preview);
	}

	private void browsePerformed() {
		String path = url.getValue();
		if (path.startsWith("./~")) {
			path = path.substring(3);
		}
		final NodeBrowserWindow nodeBrowserWindow = new NodeBrowserWindow(
				Nodes.getByPath(path));
		nodeBrowserWindow.setTitle(I18N.get("Browser"));
		nodeBrowserWindow.addCloseListener(new ActionListener() {
			public void actionPerformed(Event event) {
				if (nodeBrowserWindow.getSelectedNode() != null) {
					url.setText("./~"
							+ nodeBrowserWindow.getSelectedNode().getPath());
				}
			}
		});
		getRoot().appendChild(nodeBrowserWindow);
	}

	public void setValue(String link) {
		this.url.setValue(link);
	}

	public String getValue() {
		return this.url.getValue();
	}

	public boolean isEmpty() {
		return this.url.isEmpty();
	}

}
