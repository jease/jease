<%@page import="java.util.Set"%>
<%@page import="java.util.HashSet"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.Reference"%>
<%@page import="jease.site.Templates"%>
<%!
	final String JEASE_REFERENCE_RECURSION = "Jease.Reference.Recursion"; 
%>
<%
	Reference reference = (Reference) request.getAttribute("Node");
	if (session.getAttribute(reference.getPath()) != null) {
		reference = (Reference) session.getAttribute(reference.getPath());
	}

	Set<Reference> referenceRecursions = (Set<Reference>) request.getAttribute(JEASE_REFERENCE_RECURSION);
	if (referenceRecursions == null) {
		referenceRecursions = new HashSet<Reference>();
		request.setAttribute(JEASE_REFERENCE_RECURSION, referenceRecursions);
	}
	if (!referenceRecursions.contains(reference)) {
		referenceRecursions.add(reference);
		Content content = reference.getDestination();
		if (content != null) {
			request.setAttribute("Node", content);
			if (content.isPage()) {
				pageContext.include(Templates.get(content));
			} else {
				pageContext.forward(Templates.get(content));
			}
			request.setAttribute("Node", reference);
		}
	}
%>