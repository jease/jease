<%@page import="jease.cms.domain.*"%>
<%
	Reference reference = (Reference) request.getAttribute("Node");
	Content content = reference.getDestination();
	if (content != null) {
		request.setAttribute("Node", content);
		if (content.isPage()) {
			pageContext.include(content.getType() + ".jsp");
		} else {
			pageContext.forward(content.getType() + ".jsp");
		}
		request.setAttribute("Node", reference);
	}
%>