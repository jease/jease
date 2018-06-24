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
package jease.cms.domain;

import jease.cmf.domain.Node;
import jfix.db4o.Persistent;
import jfix.util.Reflections;

import org.apache.commons.lang3.ArrayUtils;

/**
 * A role has a name and stores an array of types (class names of content types)
 * which a user with the given role can create or edit. A role marked as
 * administrator allows a user to access privileged administration screens.
 */
public class Role extends Persistent {

	private String name;
	private String[] types;
	private boolean administrator;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String[] getTypes() {
		return types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}

	public boolean containsType(Class<? extends Node> clazz) {
		return ArrayUtils.contains(types, clazz.getName());
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	/**
	 * Returns an array of freshly instantiated Nodes from types belonging to
	 * this role.
	 */
	public Node[] getNodes() {
		Node[] nodes = new Node[types.length];
		for (int i = 0; i < nodes.length; i++) {
			nodes[i] = (Node) Reflections.newInstance(types[i]);
		}
		return nodes;
	}

	public String toString() {
		return "" + name;
	}
}
