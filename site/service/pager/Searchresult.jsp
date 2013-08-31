<%@page import="org.apache.commons.lang3.ArrayUtils"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.site.Navigations"%>
<%
	Content content = (Content) request.getAttribute("Node"); 
	Content root = (Content) request.getAttribute("Root");
%>
<p>
<%
	Content[] parents = Navigations.getBreadcrumb(root, content);
	if (ArrayUtils.isNotEmpty(parents)) {
		for (Content parent : parents) {
%>
			<a href="<%= request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a>&nbsp;&raquo;
	<% } %>
		<br />
<% } %>
	<a href="<%= request.getContextPath() %><%=content.getPath()%>"><b><%=content.getTitle()%></b></a>
</p>