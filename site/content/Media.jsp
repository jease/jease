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
	<a href="<%=media.getPath() %>?file" class="imagePopup"><img src="<%=media.getPath() %>?file" class="media" alt="<%=media.getTitle()%>" title="<%=media.getTitle()%>" /></a>
</p>
<%
	return;
	}
%>

<%
	if (media.getContentType().equals("application/x-shockwave-flash")) {
%>
<p>
	<object data="<%=media.getPath() %>?file" type="application/x-shockwave-flash" class="mediaFlash">
		<param name="movie" value="<%=media.getPath() %>?file" />
	</object>
</p>
<%
	return;
	}
%>

<%
	if (media.getContentType().startsWith("text")) {
%>
<pre class="media"><%=FileUtils.readFileToString(media.getFile())%></pre>
<%
	return;
	}
%>

<p><iframe src="<%=media.getPath() %>?file" class="media"></iframe></p>
