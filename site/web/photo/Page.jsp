<%@page import="java.util.*,jfix.util.*,jease.cms.domain.*,jease.site.*" contentType="text/html; charset=UTF-8"%>
<% Content content = (Content) request.getAttribute("Node"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<%@include file="/site/service/Pagebase.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/photo/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/photo/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
<script type="text/javascript" src="<%=request.getAttribute("Page.Root") %>site/web/photo/js/jquery.innerfade.js"></script>
<script type="text/javascript">
$(document).ready(function() { 
	$("#photos").innerfade({speed: 2000, timeout: 8000, type: "random"});	
});
</script>
</head>
<body>
	<div id="header">	
		
		<div id="photos">		
		<% for (int i=1; i < 10; i++) { %>
			<img src="<%=request.getAttribute("Page.Root") %>site/web/photo/photos/<%= i %>.jpg" alt="" width="890" height="200" />
		<% } %>
		</div>
		
		<h1><a href="<%=request.getAttribute("Page.Root") %>">jease.org</a></h1>		
		<h2>Java with Ease...</h2>				
		
		<div id="tabs">
			<ul>
			<% for (Content tab : Navigations.getTabs()) { %>
				<li<%= content.getPath().startsWith(tab.getPath() + "/") ? " class=\"current\"" : "" %>>
 					<a href="<%= tab.getPath() %>"><%= tab.getTitle() %></a>
				</li>
			<% } %>
			</ul>
		</div>		
		
		<form id="search" action="." method="get">
			<input type="text" name="query" <% if(request.getParameter("query") != null) { %>value="<%= request.getParameter("query") %>"<% } else { %>value="Search this site..." onfocus="this.value='';"<% } %> />
			<input type="hidden" name="page" value="/site/service/Search.jsp" />
		</form>				
	
		<div id="breadcrumb">
			<% 
			     boolean start = true; 
				 for (Content parent : content.getParents(Content.class)) { 
		    %>
				<% if (start) { start = false; } else { %> &raquo; <% } %>
				<a href="<%= parent.getPath() %>"><%= parent.getTitle() %></a>
			<% } %>
		</div>
		
	</div>

	<div id="main-wrapper" class="clear">
	<div id="main">
		
		<div id="content-wrapper">
		<div id="content">
				<% pageContext.include((String) request.getAttribute("Page.Template")); %>	
				<p class="editorial">
					Last modified on <%=String.format("%tF", content.getLastModified())%>
					<% if (content.getEditor() != null) { %>
						by <%= content.getEditor().getName() %>
					<% }%>
				</p>

			<% 
				News[] news = Navigations.getNews((Content) content.getParent());
				if (Validations.isNotEmpty(news)) { 
			%>
				<% for (News item : news) { %>
					<div class="news">				
					<% if (item.getDate() != null) { %>
						<div class="date"><%= String.format("%1$td %1$tb %1$tY", item.getDate()) %></div>
					<% } %>
					<% if (Validations.isEmpty(item.getTeaser())) { %>
						<h2><%= item.getTitle() %></h2>
						<%= item.getStory() %>
					<% } else { %>
						<h2><a href="<%= item.getPath() %>"><%= item.getTitle() %></a></h2>
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
					<li><a <%= item == content ? " class=\"current\"" : "" %> href="<%= item.getPath() %>"><%= item.getTitle() %></a></li>
				<% } %>
			<% } %>
			</ul>
		</div>
				 		
	</div>
	</div>
	
	<div id="footer">
		&copy; 2011 <a href="http://www.jease.org/">jease.org</a> | Design by <a href="http://www.styleshout.com/">styleshout</a>
		<br />
		<%@include file="/site/service/Designswitch.jsp" %>
	</div>

</body>
</html>