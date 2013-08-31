/*
    Copyright (C) 2013 maik.jablonski@jease.org

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
package jease.cmf.service;

import java.io.File;

import jfix.compiler.JavaCompiler;
import jfix.db4o.Database;
import jfix.functor.Supplier;

/**
 * Service for providing access to runtime Java Compiler which is aware of
 * changes in database.
 */
public class Compilers {

	private static Supplier<JavaCompiler> compiler = new Supplier<JavaCompiler>() {
		public JavaCompiler get() {
			return new JavaCompiler();
		}
	};

	/**
	 * Returns instance of class contained as java source code in given file.
	 */
	public static Object eval(File javaFile) {
		return Database.query(compiler).eval(javaFile);
	}

	/**
	 * Returns instance of class contained as java source code in given string.
	 * If the string contains only a fully qualified classname, the class is
	 * instantiated from classpath.
	 */
	public static Object eval(String source) {
		if (!source.trim().contains("\n")) {
			try {
				Class<?> clazz = Class.forName(source.trim());
				return clazz.newInstance();
			} catch (ClassNotFoundException e) {
				// pass
			} catch (IllegalAccessException e) {
				// pass
			} catch (InstantiationException e) {
				// pass
			}
		}
		return Database.query(compiler).eval(source);
	}
}
