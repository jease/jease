<%@page import="jfix.servlet.*,jfix.util.*,jease.cms.domain.*,jease.site.*,jease.*"%>
<%
	// The current node is stored in request-attribute by JeaseServletFilter.
	Content node = (Content) request.getAttribute("Node");
	
	// Save original node as stable context, because "Node" is exchanged
	// in some templates on the fly (e.g. Folder, Reference, Composite).
	request.setAttribute("Context", node);
	
	// Make Controller available (e.g. Folder dispatches content via Controller)
	request.setAttribute("Controller", request.getAttribute(jease.cmf.Names.JEASE_SITE_CONTROLLER));

	// If an Access-Object is guarding the node, use it to force authorization.
	Access access = Authorizations.check(node, request.getHeader("Authorization"));
	if (access != null) {
		// If current user can view content in the CMS, skip authorization.
		User user = (User) session.getAttribute(User.class.toString()); 
		if (user == null || !access.isDescendant(user.getRoots())) { 
			request.setAttribute("Node", node = access);
		}
	}

	// Which template should be used to render the node?
	String pageTemplate = Registry.getView(node);
	
	// If node is page-like content (e.g. text) and no file-parameter exists in request,
	// then include template, otherwise forward (e.g. to stream binary content).
	if (node.isPage() && request.getParameter("file") == null) {
		String template = request.getParameter("page");
		if (template != null && !template.startsWith("/WEB-INF") && !template.endsWith("Page.jsp") 
				&& !template.equals(request.getAttribute("Controller"))) {
			pageTemplate = template;
		}
		request.setAttribute("Page.Title", Navigations.getPageTitle(node));
		request.setAttribute("Page.Base", Navigations.getBasePath(node));
		request.setAttribute("Page.Root", Navigations.getRootPath());
		request.setAttribute("Page.Template", pageTemplate);		
		// Get design for page rendering from Registry.
		// Check if design is overwritten via request or cookie.
		// Use "default" layout if no design is set at all.
		String design = Registry.getParameter(jease.cms.Names.JEASE_SITE_DESIGN);
		design = Cookies.pick(request, response, "design", design);
		if (design != null) {
			pageContext.include(String.format("/site/web/%s/Page.jsp", design));
		}
	} else {
		pageContext.forward(pageTemplate);
	}
%>