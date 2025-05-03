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

import org.zoodb.api.impl.ZooPC;

/**
 * Base class for all persistent classes.
 * <br> Unfortunately ZooDb requires all persistent classes to be descendants of ZooPC,
 * but luckily it does not affects other engines, and could be eliminated with byte-code enhance
 * in the future, see <a href="https://github.com/tzaeschke/zoodb/issues/97">ZooPC should not be required to be super class of all persistent classes.</a>
 */
public class Persistent extends ZooPC {

    /**
     * Marker interface for value-objects. Whereas an entity (first class
     * citizen) has a lifecycle on its own, the lifecycle of a value-object is
     * dependent on enclosing entity. Therefore value-objects will be updated or
     * deleted with enclosing entity.
     */
    public interface Value {
    }

    /** Marker interface for value-objects with own blob content,
     *  so when value-object is deleted its blob must be deleted as well.
     */
    public interface ValueWithBlob extends Value {
        Blob getBlob();
    }

}
