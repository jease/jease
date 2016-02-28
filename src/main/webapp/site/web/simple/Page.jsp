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
	Content content = (Content) request.getAttribute("Node"); // = the current content object to render.
	Content root = (Content) request.getAttribute("Root"); // = the (virtual) root object for the site.
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=Navigations.getPageTitle(content)%></title>
<%-- Add screen/print stylesheets --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/simple/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/simple/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%-- Include required site services --%>
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
	<div id="header">
		<%-- Site Search: 'query' and 'page' are required attributes! --%>
		<form id="search" action="<%=request.getContextPath() %><%=root.getPath()%>" method="get">
			<input type="text" name="query" <% if(request.getParameter("query") != null) { %>value="<%= StringEscapeUtils.escapeHtml4(request.getParameter("query")) %>"<% } else { %>value="Search this site..." style="color:#a0a0a0;" onfocus="this.style.color=''; this.value='';"<% } %> />
			<input type="hidden" name="page" value="/site/service/Search.jsp" />
			<input type="submit" value="Go" />
		</form>
		<%-- Site title: the embedded link points to the homepage --%>
		<h1>
			<a href="<%=request.getContextPath() %><%=root.getPath()%>">
				<%=root.getTitle() %>
				<% if (((Folder) root).getContent() != null) { %>
					- <%= ((Folder) root).getContent().getTitle() %>
				<% } %>
			</a>
		</h1>
		<%-- Top level navigation tab menu: the current selected tab is marked with css-class "current" --%>
		<ul>
		<% for (Content tab : Navigations.getTabs(root)) { %>
			<li<%=content.getPath().startsWith(tab.getPath()) ? " class=\"current\"" : ""%>>
				<a href="<%=request.getContextPath() %><%=tab.getPath()%>"><%=tab.getTitle()%></a>
			</li>
		<% } %>
		</ul>
		<%-- Navigation Breadcrumb --%>
		<p id="breadcrumb">
		<% for (Content parent : Navigations.getBreadcrumb(root, content)) { %>
			&raquo; <a href="<%=request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a>
		<% } %>
		</p>
	</div>
	<div class="colmask threecol">
		<div class="colmid">
			<div class="colleft">
				<%-- Main content area: pageContext includes the appropriate sub-template --%>
				<div class="content">
					<% pageContext.include((String) request.getAttribute("Page.Template")); %>	
					<%-- Editorial: add some meta-information for the current document --%>
					<p id="editorial">
						<% Content latestChange = Navigations.getLatestContribution(content); %>
						Last modified on <%=String.format("%tF", latestChange.getLastModified())%>
						<% if (latestChange.getEditor() != null) { %>
							by <%=latestChange.getEditor().getName()%>
						<% }%>
					</p>
				</div>
				<%-- Sidebar navigation menu: Topics are used to mark subsections in menu --%>
				<div class="navigation">
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
				<%-- News sidebar: different rendering for news with or without teaser --%>
				<div class="news">
				<% 
					News[] news = Navigations.getNews((Content) content.getParent());
					if (ArrayUtils.isNotEmpty(news)) {
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
			</div>
			</div>
		</div>
	</div>
	<%-- Footer with integrated design selector --%>
	<div id="footer">
		<p>
		<%@include file="/site/service/Copyright.jsp" %> | 
		Liquid Layout by <a href="http://matthewjamestaylor.com/blog/perfect-3-column.htm">Matthew James Taylor</a>
		<br />
		<%@include file="/site/service/Designswitch.jsp" %>
		</p>
	</div>
</body>
</html>