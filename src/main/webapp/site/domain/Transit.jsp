<%@page import="java.io.File"%>
<%@page import="jease.cms.domain.Transit"%>
<%@page import="jease.site.Streams"%>
<%
	Transit transit = (Transit) request.getAttribute("Node");
	String contextURI = new File(pageContext.getServletContext().getRealPath("/")).toURI().toString();
	if (transit.getURI().startsWith(contextURI)) {
		String resourcePath = "/" + transit.getURI().substring(contextURI.length());
		if (transit.isForward() || request.getParameter("file") != null) {
			pageContext.forward(resourcePath);
		} else {
			pageContext.include(resourcePath);
		}
	} else {
		Streams.write(request, response, transit.getFile(), transit.getContentType());
	}
%>
