<?xml version="1.0" encoding="utf-8"?>
<%@page import="java.util.*,java.text.*,jease.cmf.service.*,jease.cms.domain.*,jease.site.*" contentType="text/xml; charset=utf-8"%>
<%
	String host = jfix.servlet.Servlets.getHost(request);
	String domain = (request.isSecure() ? "https://" : "http://") + host + request.getContextPath();
	String zone = new SimpleDateFormat("Z").format(new Date());
	DateFormat rssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'" + zone.substring(0, 3) + ":" + zone.substring(3) + "'", Locale.ENGLISH);
%>
<feed xmlns="http://www.w3.org/2005/Atom">
<link rel="self" type="application/rss+xml" href="<%=domain%>/site/service/feeds/atom.jsp" />
<title><%=Navigations.getRoot().getTitle()%> Atom Feed</title>
<subtitle>News from <%=host%></subtitle>
<id><%=domain%>/</id>
<updated><%=rssDateFormat.format(new Date())%></updated>
<% for (News item : Navigations.getSiteNews()) { %>
<entry>
<title><%=item.getTitle()%></title>
<summary><%=item.getTeaser()%></summary>
<author>
	<name><%=item.getEditor().getName()%></name>
    <email><%=item.getEditor().getEmail()%></email>
</author>
<link href="<%=domain + item.getPath()%>" />
<id><%=domain + item.getPath()%></id>
<updated><%=rssDateFormat.format(item.getDate())%></updated>
<content type="html"><![CDATA[<%=item.getStory()%>]]></content>
</entry>
<% } %>
</feed>