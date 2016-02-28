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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jfix.util.Reflections;

public class ObjectRepository {

	private final Map<Class<?>, Set<Object>> objectsByClass = new IdentityHashMap<>();

	public void put(Object entity) {
		for (Class<?> clazz : Reflections.getSuperClassesAndInterfaces(entity
				.getClass())) {
			put(clazz, entity);
		}
	}

	private void put(Class<?> clazz, Object entity) {
		Set<Object> objects = objectsByClass.get(clazz);
		if (objects == null) {
			objects = Collections.newSetFromMap(new IdentityHashMap<>());
			objectsByClass.put(clazz, objects);
		}
		objects.add(entity);
	}

	public void remove(Object entity) {
		for (Class<?> clazz : Reflections.getSuperClassesAndInterfaces(entity
				.getClass())) {
			remove(clazz, entity);
		}
	}

	private void remove(Class<?> clazz, Object entity) {
		Set<Object> objects = objectsByClass.get(clazz);
		if (objects != null) {
			objects.remove(entity);
		}
	}

	public List<Object> get(Class<?> clazz) {
		Set<Object> objects = objectsByClass.get(clazz);
		if (objects != null) {
			return new ArrayList<>(objects);
		} else {
			return new ArrayList<>();
		}
	}

	public Set<Class<?>> getClasses() {
		return objectsByClass.keySet();
	}

	public Set<Object> getReferrers(Object reference) {
		Map<Class<?>, Set<Field>> referringFieldsByClass = new IdentityHashMap<>();
		for (Class<?> clazz : getClasses()) {
			Set<Field> referringFields = Reflections.getReferringFields(clazz,
					reference);
			if (referringFields.size() > 0) {
				referringFieldsByClass.put(clazz, referringFields);
			}
		}
		Set<Object> referreringObjects = Collections
				.newSetFromMap(new IdentityHashMap<>());
		for (Class<?> clazz : referringFieldsByClass.keySet()) {
			for (Object possibleReferrer : get(clazz)) {
				if (Reflections.isReferrer(possibleReferrer,
						referringFieldsByClass.get(clazz), reference)) {
					referreringObjects.add(possibleReferrer);
				}
			}
		}
		return referreringObjects;
	}

	public Set<Object> getGarbage(Class<?> valueClazz) {
		Set<Object> orphanedValues = Collections
				.newSetFromMap(new IdentityHashMap<>());
		orphanedValues.addAll(get(valueClazz));
		orphanedValues.removeAll(Reflections.getReferredObjects(
				get(Object.class), valueClazz));
		return orphanedValues;
	}

}
