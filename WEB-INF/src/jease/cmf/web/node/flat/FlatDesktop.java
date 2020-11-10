/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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
package jease.cmf.web.node.flat;

import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeRefreshState;
import jfix.zk.ObjectTable;
import jfix.zk.Panel;
import jfix.zk.Refreshable;
import jfix.zk.Row;

public class FlatDesktop extends Row implements Refreshable {

	private ObjectTable table;
	private Panel contentPanel;
	private NodeRefreshState refreshState;

	public FlatDesktop() {	
		table = new FlatTable();
		contentPanel = new Panel(table);
		refreshState = new NodeRefreshState();
		appendChild(contentPanel);
	}

	public void refresh() {
		if (refreshState.isStale()) {
			table.refresh();
			contentPanel.setTitle(JeaseSession.getContainer().getPath());
		}
	}
}
