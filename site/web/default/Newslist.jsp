<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<%
	Content newsContext = (Content) ((Content) request.getAttribute("Node")).getParent();
	for (News news : Navigations.getNews(newsContext)) { 
%>
	<h1><a href="<%=news.getPath()%>"><%=news.getTitle()%></a></h1>
	<% if (news.getDate() != null) { %>
		<p class="news-date"><%=String.format("%tF", news.getDate())%></p>
	<% } %>	
	<% if (Validations.isEmpty(news.getTeaser())) { %>
		<%=news.getStory()%>
	<% } else { %>
		<p>
			<%=news.getTeaser()%>&nbsp;
			<a href="<%=news.getPath()%>?print">More...</a>
		</p>
	<% } %>	
<% } %>
