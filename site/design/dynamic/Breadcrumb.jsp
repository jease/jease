<%@page import="jease.cmf.domain.*"%>
<%
	for (Node parent : ((Node) request.getAttribute("Node")).getParents()) {
%>
&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a>
<%
	}
%>