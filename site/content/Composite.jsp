<%@page import="jease.cms.domain.*,jease.site.service.*"%>
<%
	Composite composite = (Composite) request.getAttribute("Node");

	for (Content child : Navigations.getItems(composite)) {
		if (child instanceof Text) {
			request.setAttribute("Node", child);
			pageContext.include(child.getType() + ".jsp");
		} else if (child instanceof Topic) {
%>
<h1><%=child.getTitle()%></h1>
<%
	} else {
%>
<ul>
	<li><a href="<%=child.getPath()%>"<%=child instanceof Image ? " class=\"imagePopup\"" : ""%>><%=child.getTitle()%></a></li>
</ul>
<%
	}
	}
%>