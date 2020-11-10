<%@page import="jfix.servlet.*,jfix.util.*,jease.cms.domain.*,jease.site.*,jease.*"%>
<%
	// The current node is stored in request-attribute by JeaseServletFilter.
	Content node = (Content) request.getAttribute("Node");
	
	// Save original node as stable context, because "Node" is exchanged
	// in some templates on the fly (e.g. Folder, Reference, Composite).
	request.setAttribute("Context", node);
	
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
				&& !template.equals((String) request.getAttribute(Names.JEASE_SITE_DISPATCHER))) {
			pageTemplate = template;
		}
		request.setAttribute("Page.Template", pageTemplate);		
		String design = Registry.getParameter(Names.JEASE_SITE_DESIGN);
		if (design != null) {
			if (design.startsWith("/")) {		
				pageContext.include(design);	
			} else {
				// Check if user design is requested via cookie.			
				String userDesign = Cookies.pick(request, response, "design", null);
				if (userDesign != null && !userDesign.contains("/")) {
					String userDesignPath = String.format("/site/web/%s/Page.jsp", userDesign);
					String userDesignRealPath = application.getRealPath(userDesignPath);
					if (userDesignRealPath != null && new java.io.File(userDesignRealPath).exists()) {
						pageContext.include(userDesignPath);
						return;
					}
				}						
				pageContext.include(String.format("/site/web/%s/Page.jsp", design));			
			}
		}
	} else {
		pageContext.forward(pageTemplate);
	}
%>