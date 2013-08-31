<%@page import="jease.cms.domain.Trash"%>
<%
	Trash trash = (Trash) request.getAttribute("Node");
	response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + trash.getParent().getPath()));
%>