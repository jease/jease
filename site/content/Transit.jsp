<%@page import="jfix.servlet.*,jease.cmf.service.*,jease.cms.domain.*"%>
<%
	Transit transit = (Transit) request.getAttribute("Node");
	String contextURI = Filenames.asURI(pageContext.getServletContext().getRealPath("/"));
	if(transit.getURI().startsWith(contextURI)) {
		String resource = "/" + transit.getURI().substring(contextURI.length());
		if(transit.isPage()) {
			pageContext.include(resource);
		} else {
			pageContext.forward(resource);
		}
	} else {
		Servlets.write(transit.getFile(), transit.getContentType(), response);
	}
%>
