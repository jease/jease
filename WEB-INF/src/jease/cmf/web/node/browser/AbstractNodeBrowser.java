/*
    Copyright (C) 2015 maik.jablonski@jease.org

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
import jfix.zk.Div;
import jfix.zk.Grid;
import jfix.zk.Panel;
import jfix.zk.Popup;
import jfix.zk.Row;
import jfix.zk.Tree;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Image;

public abstract class AbstractNodeBrowser extends Div {

	private Tree tree;
	private Grid grid;

	public AbstractNodeBrowser() {
		init();
	}

	protected abstract Button newNodeSelector(Node node);

	protected void init() {
		tree = new NodeBrowserNavigationTree();
		tree.addEventListener(Events.ON_SELECT, $event -> updateContent());
		grid = new Grid();
		grid.setMold("paging");
		appendChild(new Panel(I18N.get("Browser"), new Row(tree, grid)));
		updateContent();
	}

	private void updateContent() {
		grid.getRows().getChildren().clear();
		for (Node node : JeaseSession.getFilter().apply(
				((Node) tree.getSelectedValue()).getChildren())) {
			Button button = newNodeSelector(node);
			Popup popup = newNodePreview(node);
			if (popup != null) {
				button.setTooltip(popup);
				grid.add(button, popup);
			} else {
				grid.add(button);
			}
		}
	}

	private Popup newNodePreview(Node node) {
		String id = node.getId();
		if (id.endsWith(".jpg") || id.endsWith(".png") || id.endsWith(".gif")) {
			return new Popup(new Image(node.getPath()));
		} else {
			return null;
		}
	}
}
