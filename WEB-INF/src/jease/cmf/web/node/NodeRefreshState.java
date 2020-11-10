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
package jease.cmf.web.node;

import jease.cmf.domain.*;
import jease.cmf.service.*;
import jease.cmf.web.*;

public class NodeRefreshState {

	private long nodeChange;
	private Node nodeContainer;

	public boolean isStale() {
		long lastNodeChange = Nodes.queryLastChange();
		Node lastNodeContainer = JeaseSession.getContainer();
		if (nodeContainer != lastNodeContainer || nodeChange < lastNodeChange) {
			nodeContainer = lastNodeContainer;
			nodeChange = lastNodeChange;
			return true;
		} else {
			return false;
		}
	}
	
	public void reset() {
		nodeChange = 0;
		nodeContainer = null;
	}
}
