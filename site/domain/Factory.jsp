<%@page import="jease.cms.domain.*"%>
<%
	Factory factory = (Factory) request.getAttribute("Node");
	response.sendRedirect(request.getContextPath() + factory.getParent().getPath());
%>