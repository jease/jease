<%@page import="jease.cms.domain.*"%>
<%
	Content content = ((Reference) request.getAttribute("Node")).getContent();
	if(content != null) {
		request.setAttribute("Node", content);
		if(content.isPage()) {
			pageContext.include(content.getType() + ".jsp");
		} else {
			pageContext.forward(content.getType() + ".jsp");			
		}
	}
%>