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
package jease.cms.service;

import java.io.File;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import jfix.db4o.Database;
import jfix.db4o.Persistent;

import org.apache.commons.io.FileUtils;

public class Informatons {

	public static String getDatabaseDirectory() {
		return Database.ext().getBlobDirectory();
	}

	public static String getDatabaseSize() {
		return FileUtils.byteCountToDisplaySize(FileUtils
				.sizeOfDirectory(new File(getDatabaseDirectory())));
	}

	public static Collection<Persistent> getDatabaseObjectCount() {
		return Database.query(Persistent.class);
	}

	public static Map<Class<?>, Integer> getDatabaseClassCount() {
		Map<Class<?>, Integer> resultMap = new TreeMap<>(
				Comparator.comparing(Class::getName));
		for (Persistent obj : Database.query(Persistent.class)) {
			Class<?> clazz = obj.getClass();
			if (!resultMap.containsKey(clazz)) {
				resultMap.put(clazz, 0);
			}
			resultMap.put(clazz, resultMap.get(clazz) + 1);
		}
		return resultMap;
	}

}
