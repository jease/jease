<%@page import="java.util.*,jease.cms.domain.*"%>
<%
	String pagerScope = (String) request.getAttribute("Pager.Scope");
	List<Content> contents = (List<Content>) request.getAttribute("Pager.Contents");

	int pagerSize = request.getParameter(pagerScope + "Size") == null ? 10
			: Integer.parseInt(request.getParameter(pagerScope + "Size"));
	int pagerIndex = request.getParameter(pagerScope + "Index") == null ? 0
			: Integer.parseInt(request.getParameter(pagerScope + "Index"));
	String pagerURL = "?" + request.getQueryString().replaceAll("&" + pagerScope + "Index=[^&]*", "");
%>
<ul>
	<% for (int i = pagerIndex; i < Math.min(pagerIndex + pagerSize, contents.size()); i++) { %>
		<li><a href="<%=contents.get(i).getPath()%>"><%=contents.get(i).getTitle()%></a></li>
	<% } %>
</ul>
<% if (contents.size() > pagerSize) { %>
<p>
	<% if (pagerIndex - pagerSize >= 0) { %>
		<a href="<%=pagerURL%>&<%=pagerScope%>Index=<%=pagerIndex - pagerSize%>">&laquo;&laquo;</a>
	<% } %>
	<% for (int i = 0; i < contents.size(); i += pagerSize) { %>
		<a href="<%=pagerURL%>&<%=pagerScope%>Index=<%=i%>"><%= i == pagerIndex ? "<b>" + ((i / pagerSize) + 1) + "</b>" : (i / pagerSize) + 1%></a>
 	<% } %>
 	<% if (pagerIndex + pagerSize <= contents.size()) { %>
 		<a href="<%=pagerURL%>&<%=pagerScope%>Index=<%=pagerIndex + pagerSize%>">&raquo;&raquo;</a>
	<% } %>
</p>
<% } %>
