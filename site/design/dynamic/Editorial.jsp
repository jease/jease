<%@page import="jease.cms.domain.*"%>
<%
	Content content = ((Content) request.getAttribute("Node"));
%>
Last modified on
<%=jfix.util.Dates.YYYY_MM_DD.format(content.getLastModified())%>
by
<%=content.getEditor().getName()%>
