<%@page import="jease.cms.domain.*"%>
<%
	Folder folder = (Folder) request.getAttribute("Node");
	Content content = folder.getContent();
	if (content != null) {
		request.setAttribute("Node", content);
		pageContext.forward((String) request.getAttribute(jease.Names.JEASE_SITE_DISPATCHER));
		request.setAttribute("Node", folder);
	}
%>
