<%@page import="org.apache.commons.lang3.math.NumberUtils"%>
<%@page import="java.util.List"%>
<%@page import="jease.cms.domain.Content"%>
<%
	String pagerScope = (String) request.getAttribute("Pager.Scope");
	String pagerRenderer = (String) request.getAttribute("Pager.Renderer");
	List<Content> contents = (List<Content>) request.getAttribute("Pager.Contents");

	int pagerSize = NumberUtils.toInt(request.getParameter(pagerScope + "Size"), 10);
	int pagerIndex = NumberUtils.toInt(request.getParameter(pagerScope + "Index"), 0);
	String pagerURL = "?" + request.getQueryString().replaceAll("&" + pagerScope + "Index=[^&]*", "");
%>
<div class="<%= pagerScope %>-contents">
<%
	Content node = (Content) request.getAttribute("Node");
	for (int i = pagerIndex; i < Math.min(pagerIndex + pagerSize, contents.size()); i++) {
		request.setAttribute("Node", contents.get(i)); 
		pageContext.include(pagerRenderer);
	}
	request.setAttribute("Node", node);
%>
</div>
<% if (contents.size() > pagerSize) { %>
<p class="<%= pagerScope %>-pager">
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