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
import jfix.zk.Window;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Toolbarbutton;

public class NodeBrowserWindow extends Window {

	private Node selectedNode;

	public NodeBrowserWindow(Node container) {
		Node currentContainer = JeaseSession.getContainer();
		if (container != null) {
			JeaseSession.setContainer(container);
		}
		appendChild(new NodeBrowser());
		JeaseSession.setContainer(currentContainer);
		doModal();
	}

	public Node getSelectedNode() {
		return selectedNode;
	}

	public class NodeBrowser extends AbstractNodeBrowser {
		protected Button newNodeSelector(final Node node) {
			Button button = new Toolbarbutton(node.getId(), JeaseSession
					.getConfig().getIcon(node));
			button.addEventListener(Events.ON_CLICK,
					$event -> nodeSelectPerformed(node));
			return button;
		}
	}

	private void nodeSelectPerformed(Node node) {
		selectedNode = node;
		Events.sendEvent(this, new Event(Events.ON_CLOSE));
	}

}
