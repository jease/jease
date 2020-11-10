<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.*"%>
<%
	// The current node is stored in request-attribute by JeaseServletFilter.
	Content node = (Content) request.getAttribute("Node");
	
	// Save original node as stable context, because "Node" is exchanged
	// in some templates on the fly (e.g. Folder, Reference, Composite).
	request.setAttribute("Context", node);
	
	// Make Controller available (e.g. Folder dispatches content via Controller)
	request.setAttribute("Controller", request.getAttribute("JEASE_SITE_CONTROLLER"));

	// If an Access-Object is guarding the node, use it to force authorization.
	Access access = Authorizations.check(node, request.getHeader("Authorization"));
	if (access != null) {
		request.setAttribute("Node", node = access);		
	}

	// Set HTTP-Headers based on state of node.
	response.setDateHeader("Last-Modified", node.getLastModified().getTime());
	
	// Which template should be used to render the node?
	String pageTemplate = String.format("/site/domain/%s.jsp", node.getType());
	
	// If node is page-like content (e.g. text) and no file-parameter exists in request,
	// then include template, otherwise forward (e.g. to stream binary content).
	if (node.isPage() && request.getParameter("file") == null) {
		// If jsp-request-parameter is set, force template
		String jsp = request.getParameter("jsp");
		if (Urls.isValid(jsp) && !jsp.equals("Page")) {
			pageTemplate = String.format("%s.jsp", jsp);
		}
		request.setAttribute("Page.Title", Navigations.getPageTitle(node));
		request.setAttribute("Page.Base", Navigations.getBasePath(node));
		request.setAttribute("Page.Root", Navigations.getRootPath());
		request.setAttribute("Page.Template", pageTemplate);		
		// For demo layout use "/site/web/demo/Page.jsp".
		pageContext.include("/site/web/default/Page.jsp");
	} else {
		pageContext.forward(pageTemplate);
	}
%>