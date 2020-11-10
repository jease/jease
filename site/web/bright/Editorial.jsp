<%@page import="jease.cms.domain.*"%>
<%
	Content editorialContext = ((Content) request.getAttribute("Node"));
%>
<div class="editorial">
Last modified on <%=String.format("%tF", editorialContext .getLastModified())%>
<%
	if (editorialContext.getEditor() != null) {
%>
by <%=editorialContext.getEditor().getName()%>
<%
	}
%>
</div>