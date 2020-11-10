/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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
import jease.cmf.domain.NodeException;

/**
 * A Composite is a container, which doesn't allow other containers as children.
 * This way a Composite can be used to build complex pages out of several
 * sub-content-nodes stored within the Composite. Per default a composite is a
 * good starting point to build image gallerys or download folders.
 */
public class Composite extends Content {

	public Composite() {
	}

	public boolean isContainer() {
		return true;
	}

	public Composite copy() {
		return (Composite) super.copy();
	}

	/**
	 * Composite doesn't allow any containers as sub-nodes.
	 */
	protected void validateNesting(Node potentialChild, String potentialChildId)
			throws NodeException {
		super.validateNesting(potentialChild, potentialChildId);
		if (potentialChild.isContainer()) {
			throw new NodeException.IllegalNesting();
		}
	}
}
