<%@page import="org.jease.cms.domain.Content"%>
<%@page import="org.jease.cms.domain.Folder"%>
<%
	Folder folder = (Folder) request.getAttribute("Node");
	Content content = folder.getContent();
	if (content != null) {
		request.setAttribute("Node", content);
		pageContext.forward((String) request.getAttribute(org.jease.Names.JEASE_SITE_DISPATCHER));
		request.setAttribute("Node", folder);
	}
%>