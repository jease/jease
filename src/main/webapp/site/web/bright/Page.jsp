<%@page contentType="text/html; charset=UTF-8"%>
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
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/bright/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/bright/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp"%>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="wrap">
	<div id="header">
		<h1 id="logo">
			<a href="<%=request.getContextPath() %><%=root.getPath()%>">
			<% for(char c : root.getTitle().toCharArray()) { %><span class="<%= Character.isUpperCase(c) ? "green" : "" %>"><%=c %></span><% } %>
			</a>
		</h1>
		<% if (((Folder) root).getContent() != null) { %>
			<h2 id="slogan"><%= ((Folder) root).getContent().getTitle() %></h2>
		<% } %>
		<form method="get" class="searchform" action="<%=request.getContextPath() %><%=root.getPath()%>">
		<p>
			<input type="text" name="query" class="textbox" value="<%=request.getParameter("query") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("query")) : ""%>" />
			<input type="hidden" name="page" value="/site/service/Search.jsp" />
			<input type="submit" class="button" value="Search" />
		</p>
		</form>
		<div id="tabs">
		<ul>
		<%
			String currentPath = content.getPath();
			for (Content tab : Navigations.getTabs(root)) {
				String tabPath = tab.getPath();
				boolean current = currentPath.startsWith(tabPath + "/");
			%>
				<li<%=current ? " class=\"current\"" : ""%>>
					<a href="<%=request.getContextPath() %><%=tabPath%>"><span><%=tab.getTitle()%></span></a>
				</li>
			<%
				}
			%>
			</ul>
			</div>
		</div>
		<div id="content-wrap">
			<div id="photo"></div>
				<div id="leftbar">
					<h1><a href="<%=request.getContextPath() %><%=content.getParent().getPath()%>"><%=((Content) content.getParent()).getTitle()%></a></h1>
					<ul>
					<%
						for (Content item : Navigations.getItems((Content) content.getParent())) {
							if (item instanceof Topic) {
					%>
						</ul>
						<h1><%=item.getTitle()%></h1>
						<ul>
					<% } else { %>
					<li <%=item == content ? " class=\"current\"" : ""%>>
						<a href="<%=request.getContextPath() %><%=item.getPath()%>"><%=item.getTitle()%></a>
					</li>
					<% } %>
				<% } %>
				</ul>
			</div>
			<div id="main">
				<div id="breadcrumb">
				<% for (Content parent : Navigations.getBreadcrumb(root, content)) { %>
					&raquo; <a href="<%=request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a>
				<% } %>
				</div>
				<% pageContext.include((String) request.getAttribute("Page.Template")); %>
				<div style="clear: both"></div>
				<div id="editorial">
				<% Content latestChange = Navigations.getLatestContribution(content); %>
					Last modified on <%=String.format("%tF", latestChange.getLastModified())%>
					<% if (latestChange.getEditor() != null) { %>
						by <%=latestChange.getEditor().getName()%>
					<% }%>
				</div>
			</div>
			<div id="rightbar">
			<%
				Content newsContext = (Content) content.getParent();
				for (News news : Navigations.getNews(newsContext)) {
			%>
				<h1><a href="<%=request.getContextPath() %><%=news.getPath()%>"><%=news.getTitle()%></a></h1>
				<% if (news.getDate() != null) { %>
					<p class="news-date"><%=String.format("%tF", news.getDate())%></p>
				<% } %>	
				<% if (StringUtils.isBlank(news.getTeaser())) { %>
					<%=news.getStory()%>
				<% } else { %>
					<p>
					<%=news.getTeaser()%>
					<br />
					<a href="<%=request.getContextPath() %><%=news.getPath()%>?print">More...</a>
					</p>
				<% } %>
			<% } %>
		</div>
	</div>
	<div id="footer">
		<div class="footer-left">
			<p class="align-left">
				<%@include file="/site/service/Copyright.jsp" %>
				<br />
				<%@include file="/site/service/Designswitch.jsp" %>
			</p>
		</div>
		<div class="footer-right">
			<p class="align-right">Design by <a href="http://www.styleshout.com/">styleshout</a></p>
		</div>
	</div>
</div>
</body>
</html>