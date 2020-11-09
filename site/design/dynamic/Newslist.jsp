<%@page import="jfix.util.*,jease.cmf.domain.*,jease.cmf.service.*,jease.cms.domain.*"%>
<%
	Folder folder = (Folder) ((Node) request.getAttribute("Node"))
			.getParent();
	for (News news : folder.getVisibleChildren(News.class)) {
		if (Validations.isEmpty(news.getTeaser())) {
%>
<h1><%=news.getTitle()%></h1>
<div><%=news.getStory()%></div>
<%
	} else {
%>
<h1><%=news.getTitle()%></h1>
<p>
<%=news.getTeaser()%>
<br />
<a href="<%=news.getPath()%>">Read more...</a>
</p>
<%
	}
	}
%>
