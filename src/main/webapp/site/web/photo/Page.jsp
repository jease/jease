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
<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1" />
<link rel="stylesheet" type="text/css" media="only screen" href="<%=request.getContextPath() %>/site/web/photo/style/screen.css"  />
<link rel="stylesheet" type="text/css" media="only screen and (max-width: 925px)" href="<%=request.getContextPath() %>/site/web/photo/style/landscape.css"  />
<link rel="stylesheet" type="text/css" media="only screen and (max-width: 479px)" href="<%=request.getContextPath() %>/site/web/photo/style/portrait.css"  />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/photo/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/photo/js/jquery.innerfade.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/photo/js/jquery.mediaqueries.js"></script>
<script type="text/javascript">
$(document).ready(function() { 
	$("#photos").innerfade({speed: 2000, timeout: 8000, type: "random"});	
});
</script>
</head>
<body>
	<div id="wrap">
		<div id="header">
			<div id="photos">
				<% for (int i=1; i < 10; i++) { %>
					<img src="<%=request.getContextPath() %>/site/web/photo/photos/<%= i %>.jpg" alt="" width="890" height="200" />
				<% } %>
			</div>
			<h1><a href="<%=request.getContextPath() %><%=root.getPath()%>"><%= root.getTitle() %></a></h1>
			<% if (((Folder) root).getContent() != null) { %>
				<h2><%= ((Folder) root).getContent().getTitle() %></h2>
			<% } %>
			<div id="tabs">
				<ul>
				<% for (Content tab : Navigations.getTabs(root)) { %>
					<li<%= content.getPath().startsWith(tab.getPath() + "/") ? " class=\"current\"" : "" %>>
						<a href="<%=request.getContextPath() %><%= tab.getPath() %>"><%= tab.getTitle() %></a>
					</li>
				<% } %>
				</ul>
			</div>
			<form id="search" action="<%=request.getContextPath() %><%=root.getPath()%>" method="get">
				<input type="text" name="query" <% if(request.getParameter("query") != null) { %>value="<%= StringEscapeUtils.escapeHtml4(request.getParameter("query")) %>"<% } else { %>value="Search this site..." onfocus="this.value='';"<% } %> />
				<input type="hidden" name="page" value="/site/service/Search.jsp" />
			</form>
			<div id="breadcrumb">
				<%
					boolean start = true;
					for (Content parent : Navigations.getBreadcrumb(root, content)) {
				%>
					<% if (start) { start = false; } else { %> &raquo; <% } %>
					<a href="<%=request.getContextPath() %><%= parent.getPath() %>"><%= parent.getTitle() %></a>
				<% } %>
			</div>
		</div>
		<div id="main-wrapper" class="clear">
			<div id="main">
				<div id="content-wrapper">
					<div id="content">
						<% pageContext.include((String) request.getAttribute("Page.Template")); %>
						<div style="clear: both"></div>
						<p class="editorial">
							Last modified on <%=String.format("%tF", content.getLastModified())%>
							<% if (content.getEditor() != null) { %>
								by <%= content.getEditor().getName() %>
							<% }%>
						</p>
						<% 
							News[] news = Navigations.getNews((Content) content.getParent());
							if (ArrayUtils.isNotEmpty(news)) { 
						%>
						<% for (News item : news) { %>
							<div class="news">
								<% if (item.getDate() != null) { %>
									<div class="date"><%= String.format("%1$td %1$tb %1$tY", item.getDate()) %></div>
								<% } %>
								<% if (StringUtils.isBlank(item.getTeaser())) { %>
									<h2><%= item.getTitle() %></h2>
									<%= item.getStory() %>
								<% } else { %>
									<h2><a href="<%=request.getContextPath() %><%= item.getPath() %>"><%= item.getTitle() %></a></h2>
									<p><%= item.getTeaser() %></p>
								<% } %>
							</div>
						<% } %>
					<% } %>
					</div>
				</div>
				<div id="navigation">
					<h1><%= ((Content) content.getParent()).getTitle() %></h1>
					<ul>
					<% for (Content item : Navigations.getItems((Content) content.getParent())) { %>
						<% if (item instanceof Topic) { %>
							</ul><h1><%= item.getTitle() %></h1><ul>
						<% } else { %>
							<li><a <%= item == content ? " class=\"current\"" : "" %> href="<%=request.getContextPath() %><%= item.getPath() %>"><%= item.getTitle() %></a></li>
						<% } %>
					<% } %>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div id="footer">
		<%@include file="/site/service/Copyright.jsp" %> | Design by <a href="http://www.styleshout.com/">styleshout</a>
		<br />
		<%@include file="/site/service/Designswitch.jsp" %>
	</div>
</body>
</html>