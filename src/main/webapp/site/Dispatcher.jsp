<%@page import="java.util.Collection"%>
<%@page import="java.util.HashSet"%>
<%@page import="org.apache.commons.lang3.ArrayUtils"%>
<%@page import="jfix.servlet.Cookies"%>
<%@page import="jease.Registry"%>
<%@page import="jease.Names"%>
<%@page import="jease.cms.domain.Access"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.User"%>
<%@page import="jease.site.Authorizations"%>
<%@page import="jease.site.Templates"%>
<%
	// The current node is stored in request-attribute by JeaseServletFilter.
	Content node = (Content) request.getAttribute("Node");
	
	// Save original node as stable context, because "Node" is exchanged
	// in some templates on the fly (e.g. Folder, Reference, Composite).
	if (request.getAttribute("Context") == null) {
		request.setAttribute("Context", node);
	}
		
	// If an Access-Object is guarding the node, use it to force authorization.
	String authorization = request.getHeader("Authorization");
	Access[] guards = Authorizations.getGuards(node);
	if (ArrayUtils.isNotEmpty(guards)) {
		Access guard = Authorizations.findAuthorizingGuard(authorization, guards);
		if (guard != null) {
			if (session.getAttribute(Names.JEASE_SITE_AUTHORIZATIONS) == null) {
				session.setAttribute(Names.JEASE_SITE_AUTHORIZATIONS, new HashSet<Object>());
			}
			((Collection<Object>) session.getAttribute(Names.JEASE_SITE_AUTHORIZATIONS)).add(guard);
		} else {
			// If current user can view content in the CMS, skip authorization.
			User user = (User) session.getAttribute(User.class.toString());
			for (Access access : guards) {
				if (user == null || !access.isDescendant(user.getRoots())) {
					request.setAttribute("Node", node = access);
					break;
				}
			}
		}
	}

	// Which template should be used to render the node?
	String pageTemplate = Templates.get(node);

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
				pageContext.forward(design);
			} else {
				// Check if user design is requested via cookie.
				String userDesign = Cookies.pick(request, response, "design", null);
				if (userDesign != null && !userDesign.contains("/")) {
					String userDesignPath = String.format("/site/web/%s/Page.jsp", userDesign);
					String userDesignRealPath = application.getRealPath(userDesignPath);
					if (userDesignRealPath != null && new java.io.File(userDesignRealPath).exists()) {
						pageContext.forward(userDesignPath);
						return;
					}
				}
				pageContext.forward(String.format("/site/web/%s/Page.jsp", design));
			}
		}
	} else {
		pageContext.forward(pageTemplate);
	}
%>