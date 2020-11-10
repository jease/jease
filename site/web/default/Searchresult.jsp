<%@page import="java.util.*,jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<%
	List<Content> searchResult = Fulltexts.query(request.getParameter("query"));
	for (Content content : searchResult) {
%>
<p>
<%
	for (Content parent : content.getParents(Content.class)) {
%> 
	&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a> 
<%
 	}
 %>
	<br />
	<a href="<%=content.getPath()%>?print" 
		<%= content instanceof Image ? " class=\"imagePopup\"" : " class=\"iframePopup\"" %>><b><%=content.getTitle()%></b></a>
		(<%=content.getType()%>)
</p>
<%
	}
%>
<%
	if (searchResult.size() == 0) {
%>
<p>No results for &quot;<%=Regexps.stripTags(request.getParameter("query"))%>&quot;.</p>
<%
	}
%>
