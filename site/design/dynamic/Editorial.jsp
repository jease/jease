<%@page import="jease.cms.domain.*"%>
<%
	Content editorialContent = ((Content) request.getAttribute("Node"));
%>
Last modified on
<%=jfix.util.Dates.YYYY_MM_DD.format(editorialContent.getLastModified())%>
by
<%=editorialContent.getEditor().getName()%>
