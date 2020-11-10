<%@page import="java.util.*,jease.cms.domain.*,jease.*"%>
<%! 
	final String JEASE_REFERENCE_RECURSION = "Jease.Reference.Recursion"; 
%>
<%
	Reference reference = (Reference) request.getAttribute("Node");
	if (session.getAttribute(reference.getPath()) != null) {
		reference = (Reference) session.getAttribute(reference.getPath());
	}
	
	Set referenceRecursions = (Set) request.getAttribute(JEASE_REFERENCE_RECURSION);
	if (referenceRecursions == null) {
		referenceRecursions = new HashSet();
		request.setAttribute(JEASE_REFERENCE_RECURSION, referenceRecursions);
	}	
	if (!referenceRecursions.contains(reference)) {
		referenceRecursions.add(reference);
		
		Content content = reference.getDestination();
		if (content != null) {
			request.setAttribute("Node", content);
			if (content.isPage()) {
				pageContext.include(Registry.getView(content));
			} else {
				pageContext.forward(Registry.getView(content));
			}
			request.setAttribute("Node", reference);
		}
	}
%>