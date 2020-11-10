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
package jease.site.web;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.cmf.domain.NodeException;
import jease.cmf.web.servlet.Dispatcher;

/**
 * Default servlet to handle all requests for which no dedicated servlet is
 * declared in web.xml.
 * 
 * This implementation is Tomcat-specific. In order to have full UTF-8 support,
 * you have to add useBodyEncodingForURI="true" to Connector in server.xml.
 * 
 * For Jetty use org.mortbay.jetty.servlet.DefaultServlet as base class.
 */
public class DefaultServlet extends org.apache.catalina.servlets.DefaultServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		try {
			request.setCharacterEncoding("UTF-8");
			response.setCharacterEncoding("UTF-8");
			Dispatcher.forward(request, response, "/site/Controller.jsp");
		} catch (NodeException e) {
			super.doGet(request, response);
		}
	}

}
