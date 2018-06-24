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
package jease.cms.web.component;

import jease.cmf.service.Nodes;
import jease.cmf.web.node.browser.NodeBrowserWindow;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Images;
import jfix.zk.WebBrowser;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Toolbarbutton;

public class Linkfield extends Div {

	Textbox url = new Textbox();
	Button browse = new Button();
	Button preview = new Toolbarbutton();

	public Linkfield() {
		browse.setTooltiptext(I18N.get("Browser"));
		browse.setImage(Images.UserHome);
		browse.addEventListener(Events.ON_CLICK, $event -> browsePerformed());
		preview.setTooltiptext(I18N.get("Open"));
		preview.setImage(Images.InternetWebBrowser);
		preview.setWidth("24px");
		preview.addEventListener(Events.ON_CLICK, $event -> {
			if (StringUtils.isNotEmpty(url.getValue())) {
				getRoot().appendChild(new WebBrowser(url.getText()));
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
		nodeBrowserWindow.addEventListener(Events.ON_CLOSE, $event -> {
			if (nodeBrowserWindow.getSelectedNode() != null) {
				url.setText("./~"
						+ nodeBrowserWindow.getSelectedNode().getPath());
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

}
