/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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
package jease.cmf.web.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;

public class Dispatcher {

	public static void forward(HttpServletRequest request,
			HttpServletResponse response, String dispatcher)
			throws NodeException, IOException, ServletException {
		String uri = request.getRequestURI();
		Node node = getNodeByPath(uri
				.replaceFirst(request.getContextPath(), ""));
		if (node == null) {
			throw new NodeException();
		}
		if (node.isContainer() && !uri.endsWith("/")) {
			String queryString = request.getQueryString() != null ? "?"
					+ request.getQueryString() : "";
			response.sendRedirect(uri + "/" + queryString);
		} else {
			request.setAttribute(Node.class.getSimpleName(), node);
			request.getRequestDispatcher(dispatcher).forward(request, response);
		}
	}

	private static Node getNodeByPath(String path) {
		if (Nodes.getRoot() == null) {
			return null;
		}
		return Nodes.getRoot().getChild(path);
	}
}
