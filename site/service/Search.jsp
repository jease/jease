<%@page import="java.util.*,jfix.util.*,jease.cms.domain.*,jease.site.*,jease.site.i18n.*"%>
<h1><%= Strings.Search_results_for %> &quot;<%=Regexps.stripTags(request.getParameter("query"))%>&quot;</h1>
<%
	List<Content> contents = Fulltexts.query(request.getParameter("query"));
	if (!contents.isEmpty()) {
		request.setAttribute("Pager.Scope", "search");
		request.setAttribute("Pager.Contents", contents);
		request.setAttribute("Pager.Renderer", "/site/service/pager/Searchresult.jsp");		
		pageContext.include("/site/service/pager/Pager.jsp");
	} else {
%>
<p><%= Strings.No_results %>.</p>
<% } %>