<%@page import="jease.cms.domain.*"%>
<%
	Access access = (Access) request.getAttribute("Node");
	response.setHeader("WWW-Authenticate", "BASIC realm=\"" + access.getTitle() + "\"");
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
%>