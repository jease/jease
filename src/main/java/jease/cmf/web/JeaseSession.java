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

import jease.Names;
import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeFilter;

public class JeaseSession extends jfix.zk.Sessions {

	public static void setRoots(Node[] nodes) {
		set(Names.JEASE_SESSION_ROOTS, nodes);
	}

	public static Node[] getRoots() {
		return (Node[]) get(Names.JEASE_SESSION_ROOTS);
	}

	public static void setContainer(Node node) {
		set(Names.JEASE_SESSION_CONTAINER,
				node == null ? null : (node.isContainer() ? node : node
						.getParent()));
	}

	public static Node getContainer() {
		return (Node) get(Names.JEASE_SESSION_CONTAINER);
	}

	public static void setConfig(JeaseConfig jeaseConfig) {
		set(Names.JEASE_SESSION_CONFIG, jeaseConfig);
	}

	public static JeaseConfig getConfig() {
		return (JeaseConfig) get(Names.JEASE_SESSION_CONFIG);
	}

	public static NodeFilter getFilter() {
		return (NodeFilter) get(Names.JEASE_SESSION_FILTER);
	}

	public static void setFilter(NodeFilter nodeFilter) {
		set(Names.JEASE_SESSION_FILTER, nodeFilter);
	}
}
