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
package jfix.db4o.engine;

import java.util.Collection;

public interface PersistenceEngine {

	/**
	 * Open the specified database.
	 */
	void open(String database);

	/**
	 * Close the database.
	 */
	void close();

	/**
	 * Create a backup of the database.
	 */
	void backup();

	/**
	 * Save/Update the specified object in database.
	 */
	void save(Object object);

	/**
	 * Delete specified object from database.
	 */
	void delete(Object object);

	/**
	 * Begin transaction.
	 */
	void begin();

	/**
	 * Commit transaction.
	 */
	void commit();

	/**
	 * Rollback transaction.
	 */
	void rollback();

	/**
	 * Deliver all objects stored within database.
	 */
	Collection<Object> query();

	/**
	 * Return a directory where BLOBs should be stored.
	 */
	String getBlobDirectory();

}
