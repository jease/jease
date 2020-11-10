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
package jease.cmf.web;

import jease.cmf.domain.Node;

public class JeaseSession extends jfix.zk.Sessions {

	private static final String JEASE_ROOTS = "JEASE_ROOTS";
	private static final String JEASE_CONTAINER = "JEASE_CONTAINER";
	private static final String JEASE_CONFIG = "JEASE_CONFIG";

	public static void setRoots(Node[] nodes) {
		set(JEASE_ROOTS, nodes);
	}

	public static Node[] getRoots() {
		return (Node[]) get(JEASE_ROOTS);
	}

	public static void setContainer(Node node) {
		set(JEASE_CONTAINER, node);
	}

	public static Node getContainer() {
		return (Node) get(JEASE_CONTAINER);
	}

	public static void setConfig(JeaseConfig jeaseConfig) {
		set(JEASE_CONFIG, jeaseConfig);
	}

	public static JeaseConfig getConfig() {
		return (JeaseConfig) get(JEASE_CONFIG);
	}
}
