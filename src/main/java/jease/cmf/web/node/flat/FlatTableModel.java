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
package jease.cmf.web.node.flat;

import java.util.Arrays;
import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeTableModel;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;

public class FlatTableModel extends ObjectTableModel<Node> {

	private NodeTableModel<Node> nodeTableModel;

	public FlatTableModel(NodeTableModel<Node> nodeTableModel) {
		this.nodeTableModel = nodeTableModel;
	}

	public Node newObject() {
		return null;
	}

	public List<Node> getList() {
		return Arrays.asList(nodeTableModel.getFilter().apply(
				nodeTableModel.getContainer().getDescendants()));
	}

	public String[] getColumns() {
		String[] oldCols = nodeTableModel.getColumns();
		String[] newCols = new String[oldCols.length + 1];
		newCols[0] = I18N.get("Path");
		for (int i = 0; i < oldCols.length; i++) {
			newCols[i + 1] = oldCols[i];
		}
		return newCols;
	}

	public int[] getProportions() {
		int[] oldProps = nodeTableModel.getProportions();
		int[] newProps = new int[oldProps.length + 1];
		newProps[0] = 1;
		for (int i = 0; i < oldProps.length; i++) {
			newProps[i + 1] = oldProps[i];
			if (oldProps[i] > newProps[0]) {
				newProps[0] = oldProps[i];
			}
		}
		return newProps;
	}

	public Object getValue(final Node content, int column) {
		if (column == 0) {
			return content.getPath();
		} else {
			return nodeTableModel.getValue(content, column - 1);
		}
	}

	public Object[] getSearchValues(Node content) {
		return nodeTableModel.getSearchValues(content);
	}
}
