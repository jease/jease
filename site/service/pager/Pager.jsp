<%@page import="java.util.*,jease.cms.domain.*"%>
<%
	String pagerScope = (String) request.getAttribute("Pager.Scope");
	String pagerRenderer = (String) request.getAttribute("Pager.Renderer");
	List<Content> contents = (List<Content>) request.getAttribute("Pager.Contents");

	int pagerSize = request.getParameter(pagerScope + "Size") == null ? 10
			: Integer.parseInt(request.getParameter(pagerScope + "Size"));
	int pagerIndex = request.getParameter(pagerScope + "Index") == null ? 0
			: Integer.parseInt(request.getParameter(pagerScope + "Index"));
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
