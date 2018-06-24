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
package jease.cmf.web.node.tree.container;

import java.util.Arrays;
import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeTableModel;
import jfix.zk.ObjectTableModel;

public class ContainerTableModel extends ObjectTableModel<Node> {

	private NodeTableModel<Node> nodeTableModel;

	public ContainerTableModel(NodeTableModel<Node> nodeTableModel) {
		this.nodeTableModel = nodeTableModel;
	}

	public Node newObject() {
		return null;
	}

	public List<Node> getList() {
		return Arrays.asList(nodeTableModel.getFilter().apply(
				nodeTableModel.getContainer().getChildren()));
	}

	public String[] getColumns() {
		return nodeTableModel.getColumns();
	}

	public int[] getProportions() {
		return nodeTableModel.getProportions();
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
