<%@page import="jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<% if (news.getDate() != null) { %>
<p style="float: right;"><%=String.format("%tF", news.getDate())%></p>
<% } %>
<h1><%=news.getTitle()%></h1>
<p><strong><%=news.getTeaser()%></strong></p>
<div><%=news.getStory()%></div>
