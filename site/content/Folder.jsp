<%@page import="jease.cms.domain.*"%>
<%
	Content content = ((Folder) request.getAttribute("Node")).getDefaultContent();
	if (content != null) {
		pageContext.forward(content.getPath());
	}
%>
