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
package jease.cms.web.system.linkcheck;

import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Linkcheck;
import jease.cms.service.Linkchecker;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.ObjectTable;

import org.zkoss.zk.ui.event.Event;

public class Table extends ObjectTable {

	private Button linkcheck = new Button();

	public Table() {
		init(new TableModel());
		linkcheck.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				linkcheckPerformed();
			}
		});
		getLeftbox().appendChild(linkcheck);
	}

	private void linkcheckPerformed() {
		if (!Linkchecker.isActive()
				&& linkcheck.getLabel().equals(I18N.get("Start"))) {
			Linkchecker.start();
			Modal.info(I18N.get("In_Progress"), new ActionListener() {
				public void actionPerformed(Event event) {
					refresh();
				}
			});
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
		Linkcheck linkcheck = (Linkcheck) obj;
		Node node = Nodes.getByPath(linkcheck.getPath());
		if (node != null) {
			setEditor(Registry.getEditor(node));
			super.onSelect(node);
		} else {
			setEditor(null);
		}
	}

}
