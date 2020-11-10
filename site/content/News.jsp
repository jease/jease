<%@page import="jease.cms.domain.*"%>
<%
	News news = (News) request.getAttribute("Node");
%>
<h1><%=news.getTitle()%></h1>
<p><strong><%=news.getTeaser()%></strong></p>
<div><%=news.getStory()%></div>
