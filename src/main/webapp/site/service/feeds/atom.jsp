<%@page contentType="application/atom+xml; charset=utf-8"%>
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
	String zone = new SimpleDateFormat("Z").format(new Date());
	DateFormat rssDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'" + zone.substring(0, 3) + ":" + zone.substring(3) + "'", Locale.ENGLISH);
%>
<?xml version="1.0" encoding="utf-8"?>
<feed xmlns="http://www.w3.org/2005/Atom">
<link rel="self" type="application/atom+xml" href="<%=contextURL%>/site/service/feeds/atom.jsp" />
<title><%=StringEscapeUtils.escapeXml(((Content) request.getAttribute("Root")).getTitle())%> Atom Feed</title>
<subtitle>News from <%=Servlets.getHost(request)%></subtitle>
<id><%=contextURL%>/</id>
<updated><%=rssDateFormat.format(new Date())%></updated>
<% for (News item : Navigations.getSiteNews(((Content) request.getAttribute("Root")))) { %>
<entry>
<title><%=StringEscapeUtils.escapeXml(item.getTitle())%></title>
<summary><%=StringEscapeUtils.escapeXml(item.getTeaser())%></summary>
<author>
	<name><%=item.getEditor().getName()%></name>
	<email><%=item.getEditor().getEmail()%></email>
</author>
<link href="<%=contextURL%><%=item.getPath()%>" />
<id><%=contextURL%><%=item.getPath()%></id>
<updated><%=rssDateFormat.format(item.getDate())%></updated>
<content type="html"><![CDATA[<%=item.getStory()%>]]></content>
</entry>
<% } %>
</feed>