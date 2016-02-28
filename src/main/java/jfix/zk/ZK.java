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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jfix.servlet.Cookies;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.util.Clients;

public class ZK {

	/**
	 * Sets a cookie with given name and value.
	 */
	public static void setCookie(String name, String value) {
		Cookies.set((HttpServletResponse) Executions.getCurrent()
				.getNativeResponse(), name, value);
	}

	/**
	 * Sets a cookie with given name and value which expires in given seconds.
	 */
	public static void setCookie(String name, String value, int expires) {
		Cookies.set((HttpServletResponse) Executions.getCurrent()
				.getNativeResponse(), name, value, expires);
	}

	/**
	 * Reads a cookie with given name.
	 */
	public static String getCookie(String name) {
		return Cookies.get(((HttpServletRequest) Executions.getCurrent()
				.getNativeRequest()), name);
	}

	/**
	 * Clears all cookies by writing a null value for existing cookie names.
	 */
	public static void clearCookies() {
		for (Cookie cookie : ((HttpServletRequest) Executions.getCurrent()
				.getNativeRequest()).getCookies()) {
			ZK.setCookie(cookie.getName(), null);
		}
	}

	/**
	 * Replaces source-component with target-component.
	 */
	public static void replace(Component source, Component target) {
		Components.replace(source, target);
	}

	/**
	 * Redirect to given url.
	 */
	public static void redirect(String url) {
		Executions.getCurrent().sendRedirect(url);
	}

	/**
	 * Redirect to given url in given target window.
	 */
	public static void redirect(String url, String target) {
		Executions.getCurrent().sendRedirect(url, target);
	}

	/**
	 * Show busy indicator.
	 */
	public static void showBusy() {
		Clients.showBusy(null);
	}

	/**
	 * Clear busy indicator.
	 */
	public static void clearBusy() {
		Clients.clearBusy();
	}

	/**
	 * Retrieves the file-based path for given path of web-app-ressource. Very
	 * useful to retrieve the base folder of a web-application by using "/" as
	 * parameter.
	 */
	public static String getRealPath(String path) {
		return Executions.getCurrent().getDesktop().getWebApp()
				.getRealPath(path);
	}

	/**
	 * Returns the content-path of the web-application.
	 */
	public static String getContextPath() {
		return Executions.getCurrent().getContextPath();
	}

	/**
	 * Returns all descendants (children of children of...) for given component.
	 */
	public static List<Component> getDescendants(Component component) {
		List<Component> components = new ArrayList<>();
		components.add(component);
		for (Object child : component.getChildren()) {
			if (child instanceof Component) {
				components.addAll(getDescendants((Component) child));
			}
		}
		return components;
	}

	/**
	 * Returns the init parameter with given name set in web.xml.
	 */
	public static String getInitParameter(String name) {
		return Executions.getCurrent().getDesktop().getWebApp()
				.getInitParameter(name);
	}

	/**
	 * Returns the query string from the current request.
	 */
	public static String getQueryString() {
		return ((HttpServletRequest) Executions.getCurrent().getNativeRequest())
				.getQueryString();
	}

}
