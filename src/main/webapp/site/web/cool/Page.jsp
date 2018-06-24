<%@page contentType="text/html; charset=UTF-8"%>
<%@page import="org.apache.commons.lang3.ArrayUtils"%>
<%@page import="org.apache.commons.lang3.StringUtils"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.News"%>
<%@page import="jease.cms.domain.Topic"%>
<%@page import="jease.cms.domain.Folder"%>
<%@page import="jease.site.Navigations"%>
<%
	Content content = (Content) request.getAttribute("Node"); 
	Content root = (Content) request.getAttribute("Root");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=Navigations.getPageTitle(content) %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/cool/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/cool/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp"%>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="wrap">
	<div id="header">
		<h1 id="logo-text">
			<a href="<%=request.getContextPath() %><%=root.getPath()%>">
				<% for(char c : root.getTitle().toCharArray()) { %><span class="<%= Character.isUpperCase(c) ? "green" : "" %>"><%=c %></span><% } %>
			</a>
		</h1>
		<% if (((Folder) root).getContent() != null) { %>
			<p id="slogan"><%= ((Folder) root).getContent().getTitle() %></p>
		<% } %>
		<div id="header-links">
			<p><a href="<%=request.getContextPath() %>/cms?<%=content.getPath() %>">Edit</a></p>
		</div>
	</div>
	<div id="tabs">
		<ul>
			<% for (Content tab : Navigations.getTabs(root)) { %>
				<li<%=content.getPath().startsWith(tab.getPath() + "/") ? " class=\"current\"" : ""%>>
					<a href="<%=request.getContextPath() %><%=tab.getPath()%>"><%=tab.getTitle()%></a>
				</li>
			<% } %>
		</ul>
	</div>
	<div id="content-wrap">
		<div id="main">
			<div id="content">
				<p id="breadcrumb">
				<% for (Content parent : Navigations.getBreadcrumb(root, content)) { %>
					&raquo; <a href="<%=request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a>
				<% } %>
				</p>
				<% pageContext.include((String) request.getAttribute("Page.Template")); %>
				<div style="clear: both"></div>
				<p id="editorial">
					<% Content latestChange = Navigations.getLatestContribution(content); %>
					Last modified on <%=String.format("%tF", latestChange.getLastModified())%>
					<% if (latestChange.getEditor() != null) { %>
						by <%=latestChange.getEditor().getName()%>
					<% }%>
				</p>
			</div>
			<% 
				News[] news = Navigations.getNews((Content) content.getParent());
				if(ArrayUtils.isNotEmpty(news)) {
			%>
				<div id="news">
				<% for (News item : news) { %>
					<% if (item.getDate() != null) { %>
						<p style="float: right;"><%=String.format("%1$td %1$tb %1$tY", item.getDate())%></p>
					<% } %>
					<% if (StringUtils.isBlank(item.getTeaser())) { %>
						<h3><%=item.getTitle()%></h3>
						<%=item.getStory()%>
					<% } else { %>
						<h3><a href="<%=request.getContextPath() %><%=item.getPath()%>?print"><%=item.getTitle()%></a></h3>
						<p><%=item.getTeaser()%></p>
					<% } %>
				<% } %>
				</div>
			<% } %>
		</div>
		<div id="sidebar">
			<div id="search">
				<form method="get" action="<%=request.getContextPath() %><%=root.getPath()%>" class="searchform">
				<fieldset>
					<input type="text" class="textbox" name="query" value="<%=request.getParameter("query") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("query")) : ""%>" />
					<input type="hidden" name="page" value="/site/service/Search.jsp" />
					<input type="submit" class="button" value="Search" />
				</fieldset>
				</form>
			</div>
			<h2><%=((Content) content.getParent()).getTitle()%></h2>
			<ul>
			<% for (Content item : Navigations.getItems((Content) content.getParent())) { %>
				<% if (item instanceof Topic) { %>
					</ul><h2><%=item.getTitle()%></h2><ul>
				<% } else { %>
					<li<%=item == content ? " class=\"current\"" : ""%>><a href="<%=request.getContextPath() %><%=item.getPath()%>"><%=item.getTitle()%></a></li>
				<% } %>
			<% } %>
			</ul>
		</div>
	</div>
	<div id="footer">
		<p>
			<%@include file="/site/service/Copyright.jsp" %> | Design by <a href="http://www.styleshout.com/">styleshout</a>
			<br />
			<%@include file="/site/service/Designswitch.jsp" %>
		</p>
	</div>
</div>
</body>
</html>