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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Cookies {

    /**
     * Sets a cookie with given name and value in given response.
     */
    public static void set(HttpServletResponse response, String name, String value, int expiry) {
        Cookie cookie = new Cookie(name, value);
        cookie.setPath("/");
        cookie.setMaxAge(expiry);
        response.addCookie(cookie);
    }

    /**
     * Sets a cookie with given name and value in given response. Given expiry
     * sets the maximum age for a cookie in seconds.
     */
    public static void set(HttpServletResponse response, String name, String value) {
        set(response, name, value, -1);
    }

    /**
     * Reads a cookie with given name from given request. Returns null if cookie
     * with given name doesn't exist.
     */
    public static String get(HttpServletRequest request, String name) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    /**
     * Reads a cookie with given name from given request. Returns given
     * defaultValue if cookie with given name doesn't exist.
     */
    public static String get(HttpServletRequest request, String name, String defaultValue) {
        String value = Cookies.get(request, name);
        if (value != null) {
            return value;
        } else {
            return defaultValue;
        }
    }

    /**
     * If a request parameter with given name is set, transfer it to cookie.
     * Otherwise return cookie with given name, use given defaultValue if cookie
     * doesn't exist. Given expiry sets the maximum age for a cookie in seconds.
     */
    public static String pick(HttpServletRequest request, HttpServletResponse response,
                              String name, String defaultValue, int expiry) {
        String value = request.getParameter(name);
        if (value != null) {
            Cookies.set(response, name, value, expiry);
        } else {
            value = Cookies.get(request, name, defaultValue);
        }
        return value;
    }

    /**
     * If a request parameter with given name is set, transfer it to cookie.
     * Otherwise return cookie with given name, use given defaultValue if cookie
     * doesn't exist.
     */
    public static String pick(HttpServletRequest request, HttpServletResponse response,
                              String name, String defaultValue) {
        return pick(request, response, name, defaultValue, -1);
    }

}
