<%@page import="jfix.servlet.*,jease.cms.domain.*"%>
<%
	File file = (File) request.getAttribute("Node");		
	Servlets.write(file.getFile(), file.getContentType(), response);
%>