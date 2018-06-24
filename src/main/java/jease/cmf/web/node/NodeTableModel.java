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
package jease.cmf.web.node;

import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jfix.zk.ObjectTableModel;

public abstract class NodeTableModel<E extends Node> extends
		ObjectTableModel<E> {
	
	public E newObject() {
		return null;
	}

	public List<E> getList() {
		return null;
	}

	public Node getContainer() {
		return JeaseSession.getContainer();
	}

	public NodeFilter getFilter() {
		return JeaseSession.getFilter();
	}
}
