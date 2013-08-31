<%@page contentType="application/rss+xml; charset=utf-8"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="jfix.servlet.Servlets"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.News"%>
<%@page import="jease.site.Navigations"%>
<%
	String contextURL = Servlets.getContextURL(request);
	DateFormat rssDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH); 
%>
<?xml version="1.0" encoding="utf-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
<channel>
<atom:link href="<%=contextURL%>/site/service/feeds/rss.jsp" rel="self" type="application/rss+xml" />
<title><%=StringEscapeUtils.escapeXml(((Content) request.getAttribute("Root")).getTitle())%> RSS Feed</title>
<description>News from <%=Servlets.getHost(request)%></description>
<link><%=contextURL%>/site/service/feed/rss.jsp</link>
<pubDate><%=rssDateFormat.format(new Date()) %></pubDate>
<% for(News item : Navigations.getSiteNews(((Content) request.getAttribute("Root")))) { %>
<item>
	<title><%=StringEscapeUtils.escapeXml(item.getTitle()) %></title>
	<description><%=StringEscapeUtils.escapeXml(item.getTeaser()) %></description>
	<author><%=item.getEditor().getEmail()%> (<%=item.getEditor().getName()%>)</author>
	<link><%=contextURL%><%=item.getPath()%></link>
	<guid><%=contextURL%><%=item.getPath()%></guid>
	<pubDate><%=rssDateFormat.format(item.getDate()).replace("CET","GMT") %></pubDate>  
</item>
<% } %>
</channel>
</rss>