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
import jease.cmf.domain.NodeException;

/**
 * Container which allows to store deleted objects until the trash is emptied. A
 * Trash-Container allows to hold several objects with identical ids.
 */
public class Trash extends Content {

	public Trash() {
	}

	public boolean isContainer() {
		return true;
	}

	public boolean isPage() {
		return false;
	}

	public Trash copy(boolean recursive) {
		return (Trash) super.copy(recursive);
	}

	public boolean isEmpty() {
		return getChildren().length == 0;
	}

	protected void validateDuplicate(Node potentialChild,
			String potentialChildId) throws NodeException {
		// Duplicates allowed
	}
}