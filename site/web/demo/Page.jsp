<%@page import="jease.cms.domain.*,jease.site.*" contentType="text/html; charset=UTF-8"%>
<% Content content = (Content) request.getAttribute("Node"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<script type="text/javascript">//<![CDATA[
document.write("<base href=\"" + window.location.protocol + "//" + window.location.host + "<%=request.getAttribute("Page.Base") %>\" />");
//]]></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/demo/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/demo/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Topup.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="container">

	<div id="header">
		<h1><a href="<%=request.getAttribute("Page.Root")%>">jease.org &raquo; Java with Ease</a></h1>
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

	<div id="content">

		<div id="breadcrumb">
		<% for (Content parent : ((Content) request.getAttribute("Node")).getParents(Content.class)) { %>
			&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a>
		<% } %>
		</div>

		<% pageContext.include((String) request.getAttribute("Page.Template")); %>	

		<div id="editorial">
			Last modified on <%=String.format("%tF", content.getLastModified())%>
			<% if (content.getEditor() != null) { %>
				by <%=content.getEditor().getName()%>
			<% }%>
		</div>

		<% 
			News[] news = Navigations.getNews((Content) content.getParent());
			if(jfix.util.Validations.isNotEmpty(news)) { 
		%>
			<div id="news">			
			<% for (News item : news) { %>
				<% if (item.getDate() != null) { %>
					<p style="float: right;"><%=String.format("%tF", item.getDate())%></p>
				<% } %>
				<% if (jfix.util.Validations.isEmpty(item.getTeaser())) { %>
					<h3><%=item.getTitle()%></h3>
					<%=item.getStory()%>
				<% } else { %>
					<h3><a href="<%=item.getPath()%>?print" class="iframePopup"><%=item.getTitle()%></a></h3>
					<p><%=item.getTeaser()%></p>
				<% } %>	
			<% } %>			
			</div>
		<% } %>

	</div>

	<div id="sidebar">

		<div id="search">
			<h1>Search</h1>
			<form method="get" action=".">
			<fieldset>
				<input type="text" name="query" value="<%=request.getParameter("query") != null ? request.getParameter("query") : ""%>" />
				<input type="submit" value="Go" />
			</fieldset>
			</form>
			<% if(request.getParameter("query") != null) { %>
				<ul>
				<% for (Content item : Fulltexts.query(request.getParameter("query"))) { %>
					<li>
						<a href="<%=item.getPath()%>?print" <%= item instanceof Image ? " class=\"imagePopup\"" : " class=\"iframePopup\"" %>><%=item.getTitle()%></a>		
					</li>
				<% }%>
				</ul>
			<% } %>				
		</div>

		<div id="navigation">
			<h1><%=((Content) content.getParent()).getTitle()%></h1>
			<ul>
			<% for (Content item : Navigations.getItems((Content) content.getParent())) { %>
				<% if (item instanceof Topic) { %>
					</ul><h1><%=item.getTitle()%></h1><ul>
				<% } else { %>
					<li<%=item == content ? " class=\"current\"" : ""%>><a href="<%=item.getPath()%>"><%=item.getTitle()%></a></li>
				<% } %>
			<% } %>
			</ul>
		</div>

	</div>

	<div id="footer">
		<p>
			&copy; 2010 <a href="http://www.jease.org/">jease.org</a>
  			| Valid <a href="http://validator.w3.org/check/referer">XHTML</a> 
  			| <a href="http://jigsaw.w3.org/css-validator/check/referer">CSS</a>
			| Design by <a href="http://andreasviklund.com/">Andreas Viklund</a>
		</p>
	</div>

</div>
</body>
</html>