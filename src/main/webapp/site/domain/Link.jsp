<%@page import="jease.cms.domain.Link"%>
<%
	Link link = (Link) request.getAttribute("Node");
	if (session.getAttribute(link.getPath()) != null) {
		link = (Link) session.getAttribute(link.getPath());
	}
	response.sendRedirect(response.encodeRedirectURL(link.getUrl()));
%>