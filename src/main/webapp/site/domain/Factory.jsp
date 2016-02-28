<%@page import="jease.cms.domain.Factory"%>
<%
	Factory factory = (Factory) request.getAttribute("Node");
	response.sendRedirect(response.encodeRedirectURL(request.getContextPath() + factory.getParent().getPath()));
%>