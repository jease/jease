<%@page import="org.apache.commons.io.FileUtils,jfix.servlet.*,jease.cms.domain.*"%>
<%
	Media media = (Media) request.getAttribute("Node");

	if (request.getParameter("file") != null) {
		Servlets.write(media.getFile(), media.getContentType(), response);
		return;
	}
%>

<h1><%=media.getTitle()%></h1>

<%
	if (media.getContentType().startsWith("image")) {
%>
<p>
	<a href="<%=media.getPath() %>?file"><img src="<%=media.getPath() %>?file" alt="<%=media.getTitle()%>" title="<%=media.getTitle()%>" /></a>
</p>
<%
	return;
	}
%>

<%
	if (media.getContentType().equals("video/x-flv")) {
		String movie = String.format("%ssite/service/videoplayer/OSplayer.swf?autoplay=on&movie=%s;file", request.getAttribute("Page.Root"), media.getPath()); 
%>
<object>
 <param name="allowFullScreen" value="true" />
 <param name="quality" value="high" />
 <param name="movie" value="<%= movie %>" />
 <embed src="<%= movie %>" allowFullScreen="true" type="application/x-shockwave-flash" />
</object>
<%
	return;
	}
%>
 
<%
	if (media.getContentType().equals("application/x-shockwave-flash")) {
%>
<object data="<%=media.getPath() %>?file" type="application/x-shockwave-flash">
 <param name="movie" value="<%=media.getPath() %>?file" />
</object>
<%
	return;
	}
%>

<%
	if (media.getContentType().startsWith("text")) {
%>
<pre><%=FileUtils.readFileToString(media.getFile())%></pre>
<%
	return;
	}
%>

<iframe src="<%=media.getPath() %>?file"></iframe>
