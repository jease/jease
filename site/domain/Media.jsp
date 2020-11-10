<%@page import="org.apache.commons.io.FileUtils,jfix.util.*,jfix.servlet.*,jease.cms.domain.*"%>
<%
	Media media = (Media) request.getAttribute("Node");
	if (session.getAttribute(media.getPath()) != null) {
		media = (Media) session.getAttribute(media.getPath());
	}
	
	if (request.getParameter("file") != null) {
		if (media.getContentType().startsWith("image") && request.getParameter("scale") != null) {
			int scale = Integer.parseInt(request.getParameter("scale"));
			Servlets.write(Images.scale(media.getFile(), scale), media.getContentType(), response);
		} else {
			Servlets.write(media.getFile(), media.getContentType(), response);
		}
		return;
	}
%>
<div class="Media">
<h1 class="Title"><%=media.getTitle()%></h1>
<%
	if (media.getContentType().startsWith("image")) {
%>
<a href="<%=request.getContextPath() %><%=media.getPath() %>?file&<%=media.getContentType().replace("/",".") %>" class="Image"><img src="<%=request.getContextPath() %><%=media.getPath() %>?file" alt="<%=media.getTitle()%>" title="<%=media.getTitle()%>" /></a>
<%
	} else if (media.getContentType().equals("video/x-flv")) {
		String movie = String.format("%s/site/service/videoplayer/OSplayer.swf?autoplay=on&movie=%s%s;file", request.getContextPath(), request.getContextPath(), media.getPath()); 
%>
<object class="Video">
 <param name="allowFullScreen" value="true" />
 <param name="quality" value="high" />
 <param name="movie" value="<%= movie %>" />
 <embed src="<%= movie %>" allowFullScreen="true" type="application/x-shockwave-flash" />
</object>
<%
	} else if (media.getContentType().equals("application/x-shockwave-flash")) {
%>
<object class="Flash" data="<%=request.getContextPath() %><%=media.getPath() %>?file" type="application/x-shockwave-flash">
 <param name="movie" value="<%=request.getContextPath() %><%=media.getPath() %>?file" />
</object>
<%
	} else if (media.getContentType().startsWith("text/html")) {
%>
<div class="Html"><%=FileUtils.readFileToString(media.getFile())%></div>
<%
	} else if (media.getContentType().startsWith("text")) {
%>
<pre class="Text"><%=FileUtils.readFileToString(media.getFile())%></pre>
<%	
	} else {
%>
<iframe class="File" src="<%=request.getContextPath() %><%=media.getPath() %>?file"></iframe>
<% } %>
</div>