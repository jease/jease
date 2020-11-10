<%@page import="jfix.util.*,jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<div class="News">
<div><%= news.getDate() != null ? String.format("%tF", news.getDate()) : "" %></div>
<h1><%=news.getTitle()%></h1>
<% if (Validations.isNotEmpty(news.getTeaser())) { %>
<p><strong><%=news.getTeaser()%></strong></p>
<% } %>
<div><%=news.getStory()%></div>
</div>