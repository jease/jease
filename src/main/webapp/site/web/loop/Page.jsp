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
<title><%=Navigations.getPageTitle(content)%></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/loop/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/loop/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
	<script src='https://www.google.com/recaptcha/api.js'></script>
</head>
<body>
<div id="out-wrapper">
	<div id="wrapper">
		<div id="header">
			<h1><a href="<%=request.getContextPath() %><%=root.getPath()%>"><%=root.getTitle() %></a></h1> 
			<div id="tabs">
				<ul>
				<% for (Content tab : Navigations.getTabs(root)) { %>
					<li<%=content.getPath().startsWith(tab.getPath()) ? " class=\"current\"" : ""%>>
						<a href="<%=request.getContextPath() %><%=tab.getPath()%>"><%=tab.getTitle()%></a>
					</li>
				<% } %>
				</ul>
				<br class="clear" />
			</div>
		</div>
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
		<div id="navigation">	 
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
	<div id="news">
	<%
		News[] news = Navigations.getNews((Content) content.getParent());
		if(ArrayUtils.isNotEmpty(news)) {
			for (News item : news) {
	%>
			<h2><%=item.getTitle()%></h2>
			<% if (item.getDate() != null) { %>
				<div class="date"><%=String.format("%tF", item.getDate())%></div>
			<%} %>	
			<% if (StringUtils.isBlank(item.getTeaser())) { %>
				<%=item.getStory()%>
			<% } else { %>
				<p><%=item.getTeaser()%><br />
				<a href="<%=request.getContextPath() %><%=item.getPath()%>?print">More...</a>
				</p>
			<% } %>
		<% } %>
	<% } %>
	<div id="search">
		<h2>Search this site</h2>
		<form action="<%=request.getContextPath() %><%=root.getPath()%>" method="get">
			<input type="text" name="query" <% if(request.getParameter("query") != null) { %>value="<%= StringEscapeUtils.escapeHtml4(request.getParameter("query")) %>"<% } else { %>value="Enter your search..." onfocus="this.value='';"<% } %> />
			<input type="hidden" name="page" value="/site/service/Search.jsp" />
		</form>
		</div>
	</div>
	<br class="clear" />
	</div>
	<div id="footer">
	<p>
		<%@include file="/site/service/Copyright.jsp" %> | Design by <a href="http://connormckelvey.com">Connor McKelvey</a>
		<br />
		<%@include file="/site/service/Designswitch.jsp" %>
		</p>
	</div>
	</div>
</div>
</body>
</html>