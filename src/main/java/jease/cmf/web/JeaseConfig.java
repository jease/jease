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
package jease.cmf.web;

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeEditor;
import jease.cmf.web.node.NodeTableModel;

public interface JeaseConfig {

	/**
	 * Which type of nodes can be created by the user?
	 */
	Node[] newNodes();

	/**
	 * Which editor should be used to edit a given node?
	 */
	NodeEditor<Node> newEditor(Node node);

	/**
	 * Which TableModel should be used to render the contents of a container
	 * node?
	 */
	NodeTableModel<Node> newTableModel();

	/**
	 * Which icon should be displayed in front of a given node?
	 */
	String getIcon(Node node);
}
