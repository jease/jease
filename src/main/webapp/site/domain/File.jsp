<%@page import="jease.cms.domain.File"%>
<%@page import="jease.site.Streams"%>
<%
	File file = (File) request.getAttribute("Node");
	if (session.getAttribute(file.getPath()) != null) {
		file = (File) session.getAttribute(file.getPath());
	}
	Streams.write(request, response, file.getFile(), file.getContentType());
	return;
%>