<%@page import="jfix.util.*,jease.cmf.service.*,jease.cms.domain.*"%>
<%
	// Tomcat: add useBodyEncodingForURI="true" to Connector in server.xml
	request.setCharacterEncoding("UTF-8");
	response.setCharacterEncoding("UTF-8");

	Content node = (Content) request.getAttribute("Node");
	String pageTemplate = String.format("/site/content/%s.jsp", node.getType());
	if (node.isPage()) {
		String pageTitle = String.format("%s - %s", Nodes.getRoot().getTitle(), node.getTitle());
		if (Validations.isNotEmpty(request.getParameter("query"))) {
			pageTemplate = "/site/design/dynamic/Searchresult.jsp";
		}
		String pagePath = Nodes.getRoot().getPath();
		if (!pagePath.endsWith("/")) {
			pagePath = pagePath + "/";
		}
		request.setAttribute("Page.Title", pageTitle);
		request.setAttribute("Page.Path", pagePath);		
		request.setAttribute("Page.Template", pageTemplate);
		pageContext.include("design/dynamic/Page.jsp");
	} else {
		pageContext.forward(pageTemplate);
	}
%>