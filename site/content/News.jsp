<%@page import="jfix.util.*,jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<% if (news.getDate() != null) { %>
<p class="float-right"><i><%=Dates.YYYY_MM_DD.format(news.getDate())%></i></p>
<% } %>
<h1><%=news.getTitle()%></h1>
<p><strong><%=news.getTeaser()%></strong></p>
<div><%=news.getStory()%></div>
