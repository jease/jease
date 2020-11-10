<%@page import="jease.cms.domain.*"%>
<%
	Link link = (Link) request.getAttribute("Node");
	response.sendRedirect(link.getUrl());	
%>