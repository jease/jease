<%@page import="org.apache.commons.io.FileUtils"%>
<%@page import="jfix.util.Images"%>
<%@page import="jease.cms.domain.Media"%>
<%@page import="jease.site.Streams"%>
<%
	Media media = (Media) request.getAttribute("Node");
	String mediaPath = media.getPath();
	if (session.getAttribute(media.getPath()) != null) {
		media = (Media) session.getAttribute(media.getPath());
	}
	String contentType = media.getContentType();
	if (request.getParameter("file") != null) {
		Streams.write(request, response, media.getFile(), contentType);
		return;
	}
%>
<div class="Media">
<h1 class="Title"><%=media.getTitle()%></h1>
<% if (Images.isBrowserCompatible(contentType)) { %>
<a class="Image" href="<%=request.getContextPath() %><%=mediaPath%>?file&<%=contentType.replace("/",".") %>"><img src="<%=request.getContextPath() %><%=mediaPath%>?file" alt="<%=media.getTitle()%>" title="<%=media.getTitle()%>" /></a>
<% } else if (contentType.equals("video/x-flv") || contentType.equals("video/mp4") || contentType.equals("video/mpeg")) { %>
<object class="Video" data="<%= request.getContextPath() %>/site/service/videoplayer/flowplayer.swf" type="application/x-shockwave-flash">
	<param name="movie" value="<%= request.getContextPath() %>/site/service/videoplayer/flowplayer.swf" />
	<param name="allowfullscreen" value="true" />
	<param name="flashvars" value="config={'clip':{'url':'<%=request.getContextPath() %><%=mediaPath%>?file', 'autoPlay':false, 'autoBuffering':true}}" />
</object>
<% } else if (contentType.equals("audio/mp3") ||contentType.equals("audio/mpeg")) { %>
<object class="Audio" data="<%= request.getContextPath() %>/site/service/videoplayer/flowplayer.swf" type="application/x-shockwave-flash">
	<param name="movie" value="<%= request.getContextPath() %>/site/service/videoplayer/flowplayer.swf" />
	<param name="flashvars" value="config={'clip':{'url':'<%=request.getContextPath() %><%=mediaPath%>?file', 'autoPlay':false}, 'plugins':{'controls': {'fullscreen': false, 'autoHide': false}}}" />
</object>
<% } else if (contentType.equals("application/x-shockwave-flash")) { %>
<object class="Flash" data="<%=request.getContextPath() %><%=mediaPath%>?file" type="application/x-shockwave-flash">
 <param name="movie" value="<%=request.getContextPath() %><%=mediaPath%>?file" />
</object>
<% } else if (contentType.startsWith("text/html")) { %>
<div class="Html"><%=FileUtils.readFileToString(media.getFile())%></div>
<% } else if (contentType.startsWith("text/")) { %>
<pre class="Text"><%=FileUtils.readFileToString(media.getFile())%></pre>
<% } else { %>
<iframe class="File" src="<%=request.getContextPath() %><%=mediaPath%>?file"></iframe>
<% } %>
</div>