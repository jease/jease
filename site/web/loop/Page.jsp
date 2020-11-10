<%@page import="jease.cms.domain.*,jease.site.*" contentType="text/html; charset=UTF-8"%>
<% Content content = (Content) request.getAttribute("Node"); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<%@include file="/site/service/Pagebase.jsp"%>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%=Navigations.getPageTitle(content)%></title>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/loop/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/loop/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
</head>
<body>
<div id="out-wrapper">
  <div id="wrapper">
    
    <div id="header">
      <h1><a href="<%=request.getContextPath() %>/">JeaseCMS</a></h1> 
      <div id="tabs">
        <ul>
        <% for (Content tab : Navigations.getTabs()) { %>
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
			<% for (Content parent : content.getParents(Content.class)) { %>
				&raquo; <a href="<%=request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a>
			<% } %>
		</p>
		<% pageContext.include((String) request.getAttribute("Page.Template")); %>	
		<div style="clear: both"></div>
		<p id="editorial">
			Last modified on <%=String.format("%tF", content.getLastModified())%>
			<% if (content.getEditor() != null) { %>
				by <%=content.getEditor().getName()%>
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
			if(jfix.util.Validations.isNotEmpty(news)) {
	    		for (News item : news) { 
	    %>
			<h2><%=item.getTitle()%></h2>
			<% if (item.getDate() != null) { %>
				<div class="date"><%=String.format("%tF", item.getDate())%></div>
				<%} %>	
			<% if (jfix.util.Validations.isEmpty(item.getTeaser())) { %>						
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
		<form action="<%=request.getContextPath() %>/" method="get">
			<input type="text" name="query" <% if(request.getParameter("query") != null) { %>value="<%= request.getParameter("query") %>"<% } else { %>value="Enter your search..." onfocus="this.value='';"<% } %> />
			<input type="hidden" name="page" value="/site/service/Search.jsp" />
		</form>
		</div>	  
      </div>	  
	  <br class="clear" />
    </div>
	
    <div id="footer">
    	<p>
		&copy; 2011 <a href="http://www.jease.org/">jease.org</a> | Design by <a href="http://www.conartistdesign.com">Con(A)rtist Design</a>
		<br />
		<%@include file="/site/service/Designswitch.jsp" %>
		</p>		
	</div>
	
  </div>
</div>
</body>
</html>