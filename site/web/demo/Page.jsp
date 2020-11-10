<%@page import="jease.cms.domain.*,jease.site.*" contentType="text/html; charset=UTF-8"%>
<% Content content = (Content) request.getAttribute("Node"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<%@include file="/site/service/Pagebase.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/demo/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/demo/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="wrap">

	<div id="header">
		<h1 id="logo-text"><a href="<%=request.getAttribute("Page.Root") %>"><span class="green">j</span>e<span class="green">as</span>e.org</a></h1>		
		<p id="slogan">Java with Ease...</p>
		<div id="header-links">
			<p><a href="<%=request.getAttribute("Page.Root") %>cms?<%=content.getPath() %>">Edit</a></p>		
		</div>		
	</div>

	<div id="tabs">
		<ul>
			<% for (Content tab : Navigations.getTabs()) { %>
				<li<%=content.getPath().startsWith(tab.getPath() + "/") ? " class=\"current\"" : ""%>>
 					<a href="<%=tab.getPath()%>"><%=tab.getTitle()%></a>
				</li>
			<% } %>
		</ul>
	</div>		

	<div id="content-wrap">
	
		<div id="main">
			<div id="content">
				<p id="breadcrumb">
				<% for (Content parent : content.getParents(Content.class)) { %>
					&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a>
				<% } %>
				</p>
				<% pageContext.include((String) request.getAttribute("Page.Template")); %>	
				<p id="editorial">
					Last modified on <%=String.format("%tF", content.getLastModified())%>
					<% if (content.getEditor() != null) { %>
						by <%=content.getEditor().getName()%>
					<% }%>
				</p>
			</div>
			<% 
				News[] news = Navigations.getNews((Content) content.getParent());
				if(jfix.util.Validations.isNotEmpty(news)) { 
			%>
				<div id="news">			
				<% for (News item : news) { %>
					<% if (item.getDate() != null) { %>
						<p style="float: right;"><%=String.format("%1$td %1$tb %1$tY", item.getDate())%></p>
					<% } %>
					<% if (jfix.util.Validations.isEmpty(item.getTeaser())) { %>
						<h3><%=item.getTitle()%></h3>
						<%=item.getStory()%>
					<% } else { %>
						<h3><a href="<%=item.getPath()%>?print"><%=item.getTitle()%></a></h3>
						<p><%=item.getTeaser()%></p>
					<% } %>	
				<% } %>			
				</div>
			<% } %>
		</div>

		<div id="sidebar">
			<div id="search">			
				<form method="get" action="." class="searchform">
				<fieldset>
					<input type="text" class="textbox" name="query" value="<%=request.getParameter("query") != null ? request.getParameter("query") : ""%>" />
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
					<li<%=item == content ? " class=\"current\"" : ""%>><a href="<%=item.getPath()%>"><%=item.getTitle()%></a></li>
				<% } %>
			<% } %>
			</ul>
		</div>
	
	</div>
	
	<div id="footer">
		<p>
			&copy; 2011 <a href="http://www.jease.org/">jease.org</a> | Design by <a href="http://www.styleshout.com/">styleshout</a>
			<br />
			<%@include file="/site/service/Designswitch.jsp" %>
		</p>		
	</div>

</div>
</body>
</html>