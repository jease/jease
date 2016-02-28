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

/**
 * Base class for all persistent classes.
 */
public class Persistent {

	/**
	 * Marker interface for value-objects. Whereas an entity (first class
	 * citizen) has a lifecycle on its own, the lifecycle of a value-object is
	 * dependent on enclosing entity. Therefore value-objects will be updated or
	 * deleted with enclosing entity.
	 */
	public interface Value {
	}

}
