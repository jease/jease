<%@page import="jfix.util.*,jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	Folder newsFolder = (Folder) ((Node) request.getAttribute("Node")).getParent();
	for (News news : Navigations.getNews(newsFolder)) { 
%>
	<h1><%=news.getTitle()%></h1>
	<% if (news.getDate() != null) { %>
		<p class="float-right"><i><%=Dates.YYYY_MM_DD.format(news.getDate())%></i></p>
	<% } %>	
	<% if (Validations.isEmpty(news.getTeaser())) { %>
		<p><%=news.getStory()%></p>
	<% } else { %>
		<p>
			<%=news.getTeaser()%><br />
			<a href="<%=news.getPath()%>">Read more...</a>
		</p>
	<% } %>	
<% } %>
