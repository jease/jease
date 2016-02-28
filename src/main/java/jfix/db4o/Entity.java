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
package jfix.db4o;

import java.util.UUID;

/**
 * Base class for persistent Entities. This class provides an unique id based on
 * java.util.UUID and implements {@link #equals(Object)} and {@link #hashCode()}
 * accordingly.
 */
public class Entity extends Persistent {

	private final String id = UUID.randomUUID().toString();

	public String getId() {
		return id;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof Entity)) {
			return false;
		}
		return id.equals(((Entity) other).id);
	}

	public int hashCode() {
		return id.hashCode();
	}
}
