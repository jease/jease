<%@page import="java.util.*,jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<h1><%=I18N.get("Search_results_for")%> &quot;<%=Regexps.stripTags(request.getParameter("query"))%>&quot;</h1>
<%
	List<Content> contents = Fulltexts.query(request.getParameter("query"));
	if (!contents.isEmpty()) {
		request.setAttribute("Pager.Scope", "search");
		request.setAttribute("Pager.Contents", contents);
		request.setAttribute("Pager.Renderer", "/site/service/pager/Searchresult.jsp");		
		pageContext.include("/site/service/pager/Pager.jsp");
	} else {
%>
<p><%=I18N.get("No_results")%>.</p>
<% } %>