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
package jfix.zk;

import java.util.Collections;

import javax.servlet.http.HttpSession;

import org.zkoss.zk.ui.Executions;

public class Sessions {

	public static void set(Object object) {
		if (object != null) {
			getSession().setAttribute(object.getClass().toString(), object);
		}
	}

	public static void set(String key, Object value) {
		getSession().setAttribute(key, value);
	}

	public static <E> E get(Class<E> clazz) {
		return (E) getSession().getAttribute(clazz.toString());
	}

	public static Object get(String key) {
		return getSession().getAttribute(key);
	}

	public static <E> E get(String key, E defaultValue) {
		Object value = get(key);
		if (value == null) {
			return defaultValue;
		} else {
			return (E) value;
		}
	}

	public static void remove(Class<?> clazz) {
		remove(clazz.toString());
	}

	public static void remove(String key) {
		getSession().removeAttribute(key);
	}

	public static void invalidate() {
		HttpSession session = getSession();
		for (Object name : Collections.list(session.getAttributeNames())) {
			session.removeAttribute((String) name);
		}
		Executions.getCurrent().sendRedirect("");
	}

	public static HttpSession getSession() {
		return (HttpSession) Executions.getCurrent().getSession()
				.getNativeSession();
	}

}
