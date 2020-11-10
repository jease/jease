<%@page import="jfix.util.*,jfix.servlet.*,jease.cms.domain.*"%>
<%
	File file = (File) request.getAttribute("Node");
	if (session.getAttribute(file.getPath()) != null) {
		file = (File) session.getAttribute(file.getPath());
	}	
	if (file.getContentType().startsWith("image") && request.getParameter("scale") != null) {
		int scale = Integer.parseInt(request.getParameter("scale"));
		Servlets.write(Images.scale(file.getFile(), scale), file.getContentType(), response);
	} else {
		Servlets.write(file.getFile(), file.getContentType(), response);
	}
%>