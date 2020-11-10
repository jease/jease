<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	Content node = (Content) request.getAttribute("Node");
	
	Access access = Authorizations.check(node, request.getHeader("Authorization"));
	if (access != null) {
		request.setAttribute("Node", node = access);		
	}

	String pageTemplate = String.format("/site/content/%s.jsp", node.getType());
	if (node.isPage()) {
		// If jsp-request-parameter is set, force template
		String jsp = request.getParameter("jsp");
		if (Urls.isValid(jsp) && !jsp.equals("Page")) {
			pageTemplate = String.format("%s.jsp", jsp);
		}
		request.setAttribute("Page.Title", Navigations.getPageTitle(node));
		request.setAttribute("Page.Path", Navigations.getRootPath());
		request.setAttribute("Page.Template", pageTemplate);
		pageContext.include("design/dynamic/Page.jsp");
	} else {
		pageContext.forward(pageTemplate);
	}
%>