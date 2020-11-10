<%@page import="java.util.*,java.text.*,jease.cmf.service.*,jease.cms.domain.*,jease.site.*" contentType="text/xml; charset=utf-8"%>
<% 
  String host = jfix.servlet.Servlets.getHost(request);
  String domain = "http://" + host;
  DateFormat rssDateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH); 
%>
<?xml version="1.0" encoding="utf-8"?>
<rss version="2.0" xmlns:atom="http://www.w3.org/2005/Atom">
<channel>
<atom:link href="<%=domain%>/site/service/feeds/rss.jsp" rel="self" type="application/rss+xml" />
<title><%=((Content) Nodes.getRoot()).getTitle()%> RSS Feed</title>
<description>News from <%=host%></description>
<link><%=domain%>/site/service/feed/rss.jsp</link>
<pubDate><%= rssDateFormat.format(new Date()) %></pubDate>
<% for(News item : Navigations.getSiteNews()) { %>
<item>
  <title><%= item.getTitle() %></title>
  <description><%= item.getTeaser() %></description>
  <author><%=item.getEditor().getEmail()%> (<%=item.getEditor().getName()%>)</author>
  <link><%= domain%><%= item.getPath()%></link>
  <guid><%= domain%><%= item.getPath()%></guid>
  <pubDate><%= rssDateFormat.format(item.getDate()).replace("CET","GMT") %></pubDate>  
</item>
<% } %>
</channel>
</rss>