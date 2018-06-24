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
package jease.cms.web.system.linkcheck;

import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cms.domain.Content;
import jease.cms.domain.Linkcheck;
import jease.cms.domain.User;
import jease.cms.service.Linkchecker;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.ObjectTable;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

public class Table extends ObjectTable<Linkcheck> {

	private Button linkcheck = new Button();

	public Table() {
		init(new TableModel());
		if (JeaseSession.get(User.class).isAdministrator()) {
			initLinkcheck();
		}
	}

	private void initLinkcheck() {
		linkcheck.addEventListener(Events.ON_CLICK,
				$event -> linkcheckPerformed());
		getLeftbox().appendChild(linkcheck);
	}

	private void linkcheckPerformed() {
		if (!Linkchecker.isActive()
				&& linkcheck.getLabel().equals(I18N.get("Start"))) {
			Linkchecker.start();
			Modal.info(I18N.get("In_Progress"), $event -> refresh());
		} else {
			refresh();
		}
	}

	public void refresh() {
		if (Linkchecker.isActive()) {
			linkcheck.setLabel(I18N.get("Refresh"));
			linkcheck.setImage(Images.ViewRefresh);
		} else {
			linkcheck.setLabel(I18N.get("Start"));
			linkcheck.setImage(Images.MediaPlaybackStart);
		}
		super.refresh();
	}

	protected void onSelect(Object obj) {
		final Linkcheck linkcheck = (Linkcheck) obj;
		final Content content = (Content) Nodes.getByPath(linkcheck.getPath());
		if (content != null) {
			NodeEditor<Node> editor = Registry.getEditor(content);
			editor.addEventListener(Events.ON_CHANGE, $event -> {
				Linkchecker.clear(linkcheck.getPath());
				Linkchecker.check(content);
				refresh();
			});
			setEditor(editor);
			super.onSelect(content);
		} else {
			setEditor(null);
		}
	}

}
