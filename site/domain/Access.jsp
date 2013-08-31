<%@page import="jease.cms.domain.Access"%>
<%
	Access access = (Access) request.getAttribute("Node");
	response.setHeader("WWW-Authenticate", String.format("BASIC realm=\"%s\"", access.getTitle()));
	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
%>