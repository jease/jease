<%@page import="jfix.util.*,jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<div class="News">
<div class="Date"><%= news.getDate() != null ? String.format("%tF", news.getDate()) : "" %></div>
<h1 class="Title"><%=news.getTitle()%></h1>
<% if (Validations.isNotEmpty(news.getTeaser())) { %>
<p class="Teaser"><%=news.getTeaser()%></p>
<% } %>
<div class="Story"><%=news.getStory()%></div>
</div>