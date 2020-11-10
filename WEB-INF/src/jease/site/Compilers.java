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
package jease.site;

import java.io.File;

import jfix.db4o.Database;
import jfix.functor.Supplier;

/**
 * Service for providing access to runtime Java Compiler which is aware of
 * changes in database.
 */
public class Compilers {

	private static Supplier<jfix.util.Compiler> compiler = new Supplier() {
		public jfix.util.Compiler get() {
			return new jfix.util.Compiler();
		}
	};

	/**
	 * Returns instance of class contained as java source code in given file.
	 */
	public static Object eval(File javaFile) {
		return Database.query(compiler).eval(javaFile);
	}
}
