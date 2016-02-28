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
package jease.cmf.domain;

import jfix.util.I18N;

public class NodeException extends Exception {

	/**
	 * IllegalId is thrown when an id hasn't the correct format (e.g. by using
	 * illegal characters).
	 */
	public static class IllegalId extends NodeException {
	}

	/**
	 * IllegalDuplicate is thrown when a Node with the same id already exists in
	 * a container.
	 */
	public static class IllegalDuplicate extends NodeException {
	}

	/**
	 * IllegalNesting is thrown when a Node is not allowed to be attached as a
	 * child of a container. Most common reason: the possibility of an endless
	 * recursion. It can also thrown when a container doesn't accept certain
	 * types of Nodes.
	 */
	public static class IllegalNesting extends NodeException {
	}

	public String getMessage() {
		return I18N.get(getClass().getSimpleName());
	}
}
