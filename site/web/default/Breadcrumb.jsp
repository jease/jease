<%@page import="jease.cms.domain.*"%>
<%
	for (Content parent : ((Content) request.getAttribute("Node")).getParents(Content.class)) {
%>
&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a>
<%
	}
%>