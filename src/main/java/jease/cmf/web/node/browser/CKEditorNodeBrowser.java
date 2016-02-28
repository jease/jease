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
package jease.cmf.web.node.browser;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class CKEditorNodeBrowser extends AbstractNodeBrowser {

	private String callbackId;

	protected void init() {
		callbackId = Executions.getCurrent().getParameter("CKEditorFuncNum");
		super.init();
	}

	protected Button newNodeSelector(Node node) {
		Button button = new Toolbarbutton(node.getId(), JeaseSession
				.getConfig().getIcon(node));
		button.setWidgetListener(
				"onClick",
				String.format("window.opener.CKEDITOR.tools.callFunction(%s,'%s'); window.close();", callbackId,
						"./~" + node.getPath()));
		return button;
	}
}
