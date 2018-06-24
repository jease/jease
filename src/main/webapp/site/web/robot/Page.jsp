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
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/robot/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/web/robot/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Feeds.jsp" %>
<%@include file="/site/service/Jquery.jsp"%>
<%@include file="/site/service/Rewrite.jsp"%>
<%@include file="/site/service/Lightbox.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/robot/js/maxheight.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/robot/js/cufon-yui.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/robot/js/Myriad_Pro_300.font.js"></script>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/web/robot/js/Myriad_Pro_400.font.js"></script>
<script type="text/javascript">//<![CDATA[
Cufon.replace("#tabs", { fontFamily: 'Myriad Pro', hover:true });
Cufon.replace("#logo", { fontFamily: 'Myriad Pro', hover:true });
$(document).ready(function(){
	new ElementMaxHeight();
	Cufon.now();
});
//]]></script>
</head>
<body>
	<div id="header">
		<div class="bg">
			<div class="container">
				<div id="top">
					<div class="wrapper">
						<div class="fleft">
							<div id="logo">
								<img src="<%=request.getContextPath() %>/site/web/robot/style/img/logo.jpg" alt="" />
								<a href="<%=request.getContextPath() %><%=root.getPath()%>">
									&nbsp;
									<% for(char c : root.getTitle().toCharArray()) { %><span<%= Character.isLowerCase(c) ? " style=\"color: white;\"" : "" %>><%=c %></span><% } %>
								</a>
							</div>
						</div>
						<div class="fright">
							<form action="<%=request.getContextPath() %><%=root.getPath()%>" id="search-form">
							<fieldset>
								<input type="text" class="text" name="query" value="<%=request.getParameter("query") != null ? StringEscapeUtils.escapeHtml4(request.getParameter("query")) : ""%>" />
								<input type="hidden" name="page" value="/site/service/Search.jsp" />
								<input type="submit" class="submit" value="" />
							</fieldset>
							</form>
						</div>
					</div>
				</div>
				<div id="tabs">
					<ul>
					<% for (Content tab : Navigations.getTabs(root)) { %>
						<li><a href="<%=request.getContextPath() %><%=tab.getPath()%>"><%=tab.getTitle()%></a></li>
					<% } %>
					</ul>
				</div>
				<div id="news">
					<ul>
					<% for (News item : Navigations.getNews((Content) content.getParent())) { %>
						<li>
							<a href="<%=request.getContextPath() %><%=item.getPath()%>?print">
								<% if (item.getDate() != null) { %><%=String.format("%1$td %1$tb %1$tY", item.getDate())%> - <% } %>
								<%=item.getTitle()%>
							</a>
							<br />
							<%= StringUtils.isNotBlank(item.getTeaser()) ? item.getTeaser() : item.getStory()%>
						</li>
					<% } %>
					</ul>
				</div>
			</div>
		</div>
	</div>
	<div id="content">
		<div class="container">
			<div class="wrapper">
				<div class="aside maxheight">
					<div class="indent">
						<div id="breadcrumb">
							<% for (Content parent : Navigations.getBreadcrumb(root, content)) { %>
								<% if(parent != content.getParent()) { %>
									&raquo; <a href="<%=request.getContextPath() %><%=parent.getPath()%>"><%=parent.getTitle()%></a> 
								<% } %>
							<% } %>
						</div>
						<h1><%=((Content) content.getParent()).getTitle()%></h1>
						<ul>
						<% for (Content item : Navigations.getItems((Content) content.getParent())) { %>
							<% if (item instanceof Topic) { %>
								</ul><h2><%=item.getTitle()%></h2><ul>
							<% } else { %>
								<li><a href="<%=request.getContextPath() %><%=item.getPath()%>" <%=item == content ? " class=\"current\"" : ""%>><%=item.getTitle()%></a></li>
							<% } %>
						<% } %>
						</ul>
					</div>
				</div>
				<div class="mainContent maxheight">
					<div class="indent">
						<div class="section">
							<% pageContext.include((String) request.getAttribute("Page.Template")); %>
							<div style="clear: both"></div>
							<p class="editorial">
								<% Content latestChange = Navigations.getLatestContribution(content); %>
								Last modified on <%=String.format("%tF", latestChange.getLastModified())%>
								<% if (latestChange.getEditor() != null) { %>
									by <%=latestChange.getEditor().getName()%>
								<% }%>
							</p>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<div id="footer">
		<div class="bg">
			<div class="container">
				<div class="indent">
					<%@include file="/site/service/Copyright.jsp" %> | Design by <a href="http://www.templates.com/">templates.com</a>
					<br />
  					<%@include file="/site/service/Designswitch.jsp" %>
				</div>	
			</div>
		</div>
	</div>
</body>
</html>