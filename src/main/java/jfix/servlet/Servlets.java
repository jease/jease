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
package jfix.servlet;

import javax.servlet.http.HttpServletRequest;

public class Servlets {

	/**
	 * Returns the name of the host serving the given request. If a
	 * X-Forwarded-Host-Header is present (e.g. running behind proxy server), it
	 * will be returned, otherwise request.getServerName() (with optional port)
	 * is returned.
	 */
	public static String getHost(HttpServletRequest request) {
		if (request.getHeader("X-Forwarded-Host") != null) {
			return request.getHeader("X-Forwarded-Host");
		} else {
			return request.getServerName()
					+ (request.getServerPort() != 80 ? ":"
							+ request.getServerPort() : "");
		}
	}

	/**
	 * Returns the plain name (without port) of the host serving the given
	 * request. If a X-Forwarded-Host-Header is present (e.g. running behind
	 * proxy server), it will be returned, otherwise request.getServerName() is
	 * returned.
	 */
	public static String getServerName(HttpServletRequest request) {
		if (request.getHeader("X-Forwarded-Host") != null) {
			return request.getHeader("X-Forwarded-Host");
		} else {
			return request.getServerName();
		}
	}

	/**
	 * Returns the url of the host serving the given request.
	 * schema://authority[:port]
	 */
	public static String getServerURL(HttpServletRequest request) {
		return (request.isSecure() ? "https://" : "http://") + getHost(request);
	}

	/**
	 * Returns the server url with context path for the given request.
	 */
	public static String getContextURL(HttpServletRequest request) {
		return (request.isSecure() ? "https://" : "http://") + getHost(request)
				+ request.getContextPath();
	}

}
