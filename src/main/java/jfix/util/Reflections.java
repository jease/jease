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
package jfix.util;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Common utility-methods to handle reflection.
 */
public class Reflections {

	/**
	 * Returns all fields of the referring class which might hold references to
	 * given reference object. The reference object is used as "template object"
	 * (and not as class) to make use of the
	 * java.lang.reflect.Type.isInstance()-method.
	 */
	public static Set<Field> getReferringFields(Class<?> referringClazz,
			Object reference) {
		Set<Field> referringFields = new HashSet<Field>();
		for (Field possibleReferringField : getFields(referringClazz)) {
			if (isAssignable(reference.getClass(), possibleReferringField)) {
				referringFields.add(possibleReferringField);
			}
		}
		return referringFields;
	}

	/**
	 * Returns true if given possible referrer is referencing given reference
	 * object in one or more of the given referring fields.
	 */
	public static boolean isReferrer(Object possibleReferrer,
			Set<Field> referringFields, Object reference) {
		try {
			for (Field field : referringFields) {
				Object fieldValue = field.get(possibleReferrer);
				if (fieldValue != null
						&& (fieldValue == reference || (field.getType()
								.isArray() && ArrayUtils.contains(
								(Object[]) fieldValue, reference)))) {
					return true;
				}
			}
			return false;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns all interfaces and superclasses implemented by a given class.
	 */
	public static Set<Class<?>> getSuperClassesAndInterfaces(Class<?> clazz) {
		Set<Class<?>> result = new HashSet<>();
		if (clazz != null) {
			result.add(clazz);
			for (Class<?> interfaceClass : clazz.getInterfaces()) {
				result.addAll(getSuperClassesAndInterfaces(interfaceClass));
			}
			result.addAll(getSuperClassesAndInterfaces(clazz.getSuperclass()));
		}
		return result;
	}

	/**
	 * Returns all fields of a given class (including fields from superclasses).
	 */
	public static Set<Field> getFields(Class<?> clazz) {
		Set<Field> result = new HashSet<>();
		for (Class<?> superClass : getSuperClassesAndInterfaces(clazz)) {
			for (Field field : superClass.getDeclaredFields()) {
				field.setAccessible(true);
				result.add(field);
			}
		}
		return result;
	}

	/**
	 * Retrieves all objects of given class-type which are referred by all the
	 * objects in given collection.
	 */
	public static Set<Object> getReferredObjects(Collection<?> objects,
			Class<?> objectClass) {
		try {
			Set<Object> result = new HashSet<>();
			for (Object object : objects) {
				for (Field field : Reflections.getFields(object.getClass())) {
					if (isAssignable(objectClass, field)) {
						Object fieldValue = field.get(object);
						if (fieldValue != null) {
							if (field.getType().isArray()) {
								for (Object arrayElement : (Object[]) fieldValue) {
									result.add(arrayElement);
								}
							} else {
								result.add(fieldValue);
							}
						}
					}
				}
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns all instanceable (sub-)classes of given type in given package.
	 */
	public static <E> E[] find(Class<E> classType, Package pckage) {
		File directory;
		try {
			String name = "/" + pckage.getName().replace('.', '/');
			directory = new File(classType.getResource(name).toURI());
		} catch (URISyntaxException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		List<E> result = new ArrayList<>();
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class")) {
					String classname = files[i].substring(0,
							files[i].length() - 6);
					try {
						Object o = Class.forName(
								pckage.getName() + "." + classname)
								.newInstance();
						if (classType.isInstance(o)) {
							result.add((E) o);
						}
					} catch (ClassNotFoundException cnfex) {
						System.err.println(cnfex);
					} catch (InstantiationException iex) {
					} catch (IllegalAccessException iaex) {
					}
				}
			}
		}
		result.sort(new Comparator<Object>() {
			public int compare(Object o1, Object o2) {
				return o1.getClass().getSimpleName()
						.compareTo(o2.getClass().getSimpleName());
			}
		});
		return result
				.toArray((E[]) Array.newInstance(classType, result.size()));
	}

	/**
	 * Returns all instanceable (sub-)classes of given type contained in the
	 * package of given type.
	 */
	public static <E> E[] find(Class<E> classType) {
		return find(classType, classType.getPackage());
	}

	/**
	 * Returns a new instance for given clazz.
	 */
	public static Object newInstance(Class<?> clazz) {
		try {
			return clazz.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns a new instance for given fully qualified classname.
	 */
	public static Object newInstance(String classname) {
		try {
			return Class.forName(classname).newInstance();
		} catch (ReflectiveOperationException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Returns true if given class can be assigned to given field (which might
	 * be a simple field or array).
	 */
	public static boolean isAssignable(Class<?> clazz, Field field) {
		return clazz.isAssignableFrom(field.getType())
				|| (field.getType().isArray() && clazz.isAssignableFrom(field
						.getType().getComponentType()));
	}

	/**
	 * Initializes all declared static string fields in given class with name of
	 * fields.
	 */
	public static void init(Class<?> clazz) {
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			try {
				field.set(null, field.getName());
			} catch (Exception e) {
			}
		}
	}
}
