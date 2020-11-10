<%@page import="jease.cms.domain.*,jease.*"%>
<%
	Reference reference = (Reference) request.getAttribute("Node");
	Content content = reference.getDestination();
	if (content != null) {
		request.setAttribute("Node", content);
		if (content.isPage()) {
			pageContext.include(Registry.getView(content));
		} else {
			pageContext.forward(Registry.getView(content));
		}
		request.setAttribute("Node", reference);
	}
%>