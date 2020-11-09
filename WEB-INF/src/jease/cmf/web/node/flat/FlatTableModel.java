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
package jease.cmf.web.node.flat;

import java.util.ArrayList;
import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.i18n.Strings;
import jease.cmf.web.node.NodeTableModel;
import jfix.functor.Procedure;
import jfix.zk.ObjectTableModel;

public class FlatTableModel extends ObjectTableModel<Node> {

	private NodeTableModel nodeTableModel;

	public FlatTableModel() {
		nodeTableModel = JeaseSession.getConfig().newTableModel();
	}

	public Node newObject() {
		return null;
	}

	public List<Node> getList() {
		final List<Node> nodes = new ArrayList();
		Procedure<Node> addNode = new Procedure<Node>() {
			public void execute(Node node) {
				nodes.add(node);
			}
		};
		JeaseSession.getContainer().traverse(addNode);
		return nodes;
	}

	public String[] getColumns() {
		String[] oldCols = nodeTableModel.getColumns();
		String[] newCols = new String[oldCols.length + 1];
		newCols[0] = Strings.Path;
		for (int i = 0; i < oldCols.length; i++) {
			newCols[i + 1] = oldCols[i];
		}
		return newCols;
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
