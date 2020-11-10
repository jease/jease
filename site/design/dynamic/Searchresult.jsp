<%@page import="java.util.*,jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	List<Content> searchResult = FullTexts.query(request.getParameter("query"));
	for (Content content : searchResult) {
%>
<p>
<%
	for (Node parent : content.getParents()) {
%> 
	&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a> 
<%
 	}
 %>
	<br />
	<a href="<%=content.getPath()%>"><b><%=content.getTitle()%></b></a> (<%=content.getType()%>)
</p>
<%
	}
%>
<%
	if (searchResult.size() == 0) {
%>
<p>No results for &quot;<%=request.getParameter("query")%>&quot;.</p>
<%
	}
%>
