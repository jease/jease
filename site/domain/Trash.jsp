<%@page import="jease.cms.domain.*"%>
<%
	Trash trash = (Trash) request.getAttribute("Node");
	response.sendRedirect(request.getContextPath() + trash.getParent().getPath());
%>