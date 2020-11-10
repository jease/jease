<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<%
	Content newsContext = (Content) ((Content) request.getAttribute("Node")).getParent();
	for (News news : Navigations.getNews(newsContext)) { 
%>
	<h1><a href="<%=news.getPath()%>"><%=news.getTitle()%></a></h1>
	<% if (news.getDate() != null) { %>
		<p class="float-right"><i><%=Dates.YYYY_MM_DD.format(news.getDate())%></i></p>
	<% } %>	
	<% if (Validations.isEmpty(news.getTeaser())) { %>
		<%=news.getStory()%>
	<% } else { %>
		<p>
			<%=news.getTeaser()%>&nbsp;
			<a href="<%=news.getPath()%>?print" class="iframePopup">More...</a>
		</p>
	<% } %>	
<% } %>
