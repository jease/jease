<%@page import="jease.cms.domain.*,jease.site.*" contentType="text/html; charset=UTF-8"%>
<% Content content = (Content) request.getAttribute("Node"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<%@include file="/site/service/Pagebase.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/paper/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/paper/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<!--[if IE]>
<style type="text/css"> 
  .twoColFixRtHdr #content { zoom: 1; }
</style>
<![endif]-->
<%@include file="/site/service/Topup.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="container">

	<div id="header">
		<h1><a href="<%=request.getAttribute("Page.Root")%>">&nbsp;jease.org</a></h1>
		<h2>Java with Ease...</h2>
	</div>

	<div id="tabs">
		<ul>
		<% for (Content tab : Navigations.getTabs()) { %>
			<li>&nbsp;<a href="<%=tab.getPath()%>"><%=tab.getTitle()%></a>&nbsp;</li>
		<% } %>
		</ul>
	</div>

	<div id="sidebar">			
		<h3><%=((Content) content.getParent()).getTitle()%></h3>
		<ul>
		<% for (Content item : Navigations.getItems((Content) content.getParent())) { %>
			<% if (item instanceof Topic) { %>
				</ul><h3><%=item.getTitle()%></h3><ul>
			<% } else { %>
				<li><a href="<%=item.getPath()%>"><%=item.getTitle()%></a></li>
			<% } %>
		<% } %>
		</ul>
	</div>

	<div id="content">

		<div id="breadcrumb">
		<% for (Content parent : ((Content) request.getAttribute("Node")).getParents(Content.class)) { %>
			&raquo; <a href="<%=parent.getPath()%>"><%=parent.getTitle()%></a>
		<% } %>
		</div>
		
		<div class="article">
			<% pageContext.include((String) request.getAttribute("Page.Template")); %>			
			<h3 class="editorial">
				Last modified on <%=String.format("%tF", content.getLastModified())%>
				<% if (content.getEditor() != null) { %>
					by <%=content.getEditor().getName()%>
				<% }%>
			</h3>
		</div>

		<div id="news">
		<% 
			News[] news = Navigations.getNews((Content) content.getParent());
			if(jfix.util.Validations.isNotEmpty(news)) { 
		%>
			<div class="article">						
			<% for (News item : news) { %>
				<div class="separator">&nbsp;</div>
				<% if (item.getDate() != null) { %>
					<p class="floatR"><%=String.format("%tF", item.getDate())%></p>
				<% } %>														
				<% if (jfix.util.Validations.isEmpty(item.getTeaser())) { %>
					<h2><%=item.getTitle()%></h2>					
					<%=item.getStory()%>
				<% } else { %>
					<h2><a href="<%=item.getPath()%>?print" class="iframePopup"><%=item.getTitle()%></a></h2>
					<p><%=item.getTeaser()%></p>
				<% } %>				
			<% } %>				
			</div>			
		<% } %>
		</div>	
	
	</div>

	<div id="footer">
		<p>
			&copy; 2010 <a href="http://www.jease.org/">jease.org</a>
  			<br />
  			<%@include file="/site/service/Designswitch.jsp" %>	  			  			
		</p>		
	</div>

</div>
</body>
</html>