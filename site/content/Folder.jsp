<%@page import="jease.cms.domain.*"%>
<%
	Content content = ((Folder) request.getAttribute("Node")).getDefaultContent();
	if (content != null) {
		request.setAttribute("Node", content);
		pageContext.forward(pageContext.getServletContext().getInitParameter("JEASE_SITE_CONTROLLER"));
	}
%>
