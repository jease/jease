/*
    Copyright (C) 2011 maik.jablonski@jease.org

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
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Div;
import jfix.zk.Grid;
import jfix.zk.Image;
import jfix.zk.Linkbutton;
import jfix.zk.Panel;
import jfix.zk.Popup;
import jfix.zk.Row;
import jfix.zk.Tree;

import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;

public class NodeBrowser extends Div {

	private static String CALLBACK_SCRIPT = "window.opener.CKEDITOR.tools.callFunction(%s,'%s'); window.close();";

	private Tree tree;
	private Grid grid;
	private String callbackId;

	public NodeBrowser() {
		callbackId = Executions.getCurrent().getParameter("CKEditorFuncNum");
		tree = new NodeBrowserNavigationTree();
		tree.addSelectListener(new ActionListener() {
			public void actionPerformed(Event event) {
				updateContent();
			}
		});
		grid = new Grid();
		grid.setMold("paging");
		appendChild(new Panel(I18N.get("Browser"), new Row(tree, grid)));
		updateContent();
	}

	private void updateContent() {
		grid.getRows().getChildren().clear();
		for (Node node : JeaseSession.getFilter().apply(
				((Node) tree.getSelectedValue()).getChildren())) {
			Button button = newButton(node);
			Popup popup = newPopup(node);
			if (popup != null) {
				button.setTooltip(popup);
				grid.add(button, popup);
			} else {
				grid.add(button);
			}
		}
	}

	private Button newButton(Node node) {
		Linkbutton button = new Linkbutton(node.getId(), JeaseSession
				.getConfig().getIcon(node));
		button.setWidgetListener("onClick", String.format(CALLBACK_SCRIPT,
				callbackId, "./~" + node.getPath()));
		return button;
	}

	private Popup newPopup(Node node) {
		String id = node.getId();
		if (id.endsWith(".jpg") || id.endsWith(".png") || id.endsWith(".gif")) {
			return new Popup(new Image(node.getPath()));
		} else {
			return null;
		}
	}
}
