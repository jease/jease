<%@page import="jfix.servlet.*,jease.cms.domain.*"%>
<%
	Transit transit = (Transit) request.getAttribute("Node");		
	Servlets.write(transit.getFile(), transit.getContentType(), response);
%>