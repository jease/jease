<%@page import="java.util.*,jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<h1>Search results for &quot;<%=Regexps.stripTags(request.getParameter("query"))%>&quot;</h1>
<%
	List<Content> contents = Fulltexts.query(request.getParameter("query"));
	if (!contents.isEmpty()) {
		request.setAttribute("Pager.Scope", "search");
		request.setAttribute("Pager.Contents", contents);
		pageContext.include("/site/service/Pager.jsp");
	} else {
%>
<p>No results.</p>
<% } %>