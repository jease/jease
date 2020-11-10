<%@page import="jfix.util.*,jease.cmf.service.*,jease.cms.domain.*"%>
<%
	Content node = (Content) request.getAttribute("Node");
	String pageTemplate = String.format("/site/content/%s.jsp", node.getType());
	if (node.isPage()) {
		if (Validations.isNotEmpty(request.getParameter("query"))) {
			pageTemplate= "/site/design/dynamic/Searchresult.jsp";
		}
		String rootPath = Nodes.getRoot().getPath();
		if(!rootPath.endsWith("/")) {
			rootPath = rootPath + "/";
		}
		request.setAttribute("Page.Path", rootPath);
		request.setAttribute("Page.Template", pageTemplate);
		pageContext.include("design/dynamic/Page.jsp");
	} else {
		pageContext.forward(pageTemplate);
	}
%>