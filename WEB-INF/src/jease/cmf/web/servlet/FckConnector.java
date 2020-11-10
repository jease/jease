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
package jease.cmf.web.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jfix.util.Arrays;

public class FckConnector extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		response.setContentType("application/xml");
		response.setCharacterEncoding("UTF-8");
		response.setHeader("Cache-Control", "no-cache");		
		
		String currentFolder = request.getParameter("CurrentFolder");
		Node[] roots = (Node[]) request.getSession().getAttribute(JeaseSession.JEASE_ROOTS);				
		
		if (currentFolder.equals("/") && !Arrays.contains(roots, Nodes.getRoot())) {
			writeRootsXML(response.getWriter(), roots);
		} else {
			writeCurrentFolderXML(response.getWriter(), roots, currentFolder);
		}
		response.flushBuffer();
	}
	
	private void writeRootsXML(PrintWriter out, Node[] roots) {
		out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		out.println("<Connector>");
		out.println("<CurrentFolder path=\"/\" url=\"/\" />");
		out.println("<Folders>");
		for (Node subnode : roots) {
			out.println("<Folder name=\"" + subnode.getId() + "\" />");
		}
		out.println("</Folders>");
		out.println("<Files>");
		out.println("</Files>");
		out.println("</Connector>");
	}
	
	private void writeCurrentFolderXML(PrintWriter out, Node[] roots, String currentFolder) {
		Node node = null;
		for(Node virtualRoot : roots) {
			if(("/" + virtualRoot.getId() + "/").equals(currentFolder)) {
				node = virtualRoot;
				break;
			}
			node = virtualRoot.getChild(currentFolder);
			if(node != null) {
				break;
			}			
		}

		String nodePath = node.getPath();
		String currentFolderPath = nodePath.endsWith("/") ? nodePath : nodePath + "/";

		out.println("<?xml version=\"1.0\" encoding=\"utf-8\" ?>");
		out.println("<Connector>");
		out.println("<CurrentFolder path=\"" + currentFolderPath
				+ "\" url=\"" + currentFolderPath + "\" />");
		out.println("<Folders>");
		for (Node subnode : node.getChildren()) {
			if (subnode.isContainer()) {
				out.println("<Folder name=\"" + subnode.getId() + "\" />");
			}
		}
		out.println("</Folders>");
		out.println("<Files>");
		for (Node subnode : node.getChildren()) {
			if (!subnode.isContainer()) {
				out.println("<File name=\"" + subnode.getId()
						+ "\" size=\"" + subnode.getSize() + "\" />");
			}
		}
		out.println("</Files>");
		out.println("</Connector>");
	}
}