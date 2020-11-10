<%@page import="jfix.util.*,jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<% if (news.getDate() != null) { %>
<p style="float: right;"><%=String.format("%tF", news.getDate())%></p>
<% } %>
<h1><%=news.getTitle()%></h1>
<% if (Validations.isNotEmpty(news.getTeaser())) { %>
<p><strong><%=news.getTeaser()%></strong></p>
<% } %>
<%=news.getStory()%>
