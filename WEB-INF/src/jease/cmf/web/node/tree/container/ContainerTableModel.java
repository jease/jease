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
package jease.cmf.web.node.tree.container;

import java.util.*;

import jease.cmf.domain.*;
import jease.cmf.web.node.*;
import jfix.zk.*;

public class ContainerTableModel extends ObjectTableModel<Node> {

	private NodeTableModel nodeTableModel;

	public ContainerTableModel(NodeTableModel nodeTableModel) {
		this.nodeTableModel = nodeTableModel;
	}

	public Node newObject() {
		return null;
	}

	public List<Node> getList() {
		return Arrays.asList(nodeTableModel.getContainer().getChildren());
	}

	public String[] getColumns() {
		return nodeTableModel.getColumns();
	}

	public Object getValue(Node content, int column) {
		return nodeTableModel.getValue(content, column);
	}

	public Object[] getSearchValues(Node content) {
		return nodeTableModel.getSearchValues(content);
	}

	public boolean isSortable() {
		return false;
	}
}
