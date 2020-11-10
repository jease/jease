<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	// The current Node is stored in request-attribute by DefaultServlet.
	Content node = (Content) request.getAttribute("Node");
	
	// If an Access-Object is protecting the Node,
	// use it instead of Node to force Authorization.
	Access access = Authorizations.check(node, request.getHeader("Authorization"));
	if (access != null) {
		request.setAttribute("Node", node = access);		
	}

	// Which template should be used to render the Node?
	String pageTemplate = String.format("/site/content/%s.jsp", node.getType());
	
	// If Node is page-like content (e.g. text) and no file-parameter exists in request,
	// then include template, otherwise forward (e.g. to stream binary content).
	if (node.isPage() && request.getParameter("file") == null) {
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