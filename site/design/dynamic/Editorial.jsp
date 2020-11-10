<%@page import="jease.cms.domain.*"%>
<%
	Content editorialContext = ((Content) request.getAttribute("Node"));
%>
Last modified on <%=jfix.util.Dates.YYYY_MM_DD.format(editorialContext .getLastModified())%>
<%
	if (editorialContext.getEditor() != null) {
%>
by <%=editorialContext.getEditor().getName()%>
<%
	}
%>
