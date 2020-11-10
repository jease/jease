<%@page import="jease.cms.domain.*,jease.site.*,jease.*"%>
<% 
	Composite composite = (Composite) request.getAttribute("Node");
%>
<div class="Composite">
<%
	for (Content child : Navigations.getVisibleContent(composite)) {
		if (child.isPage()) {
			request.setAttribute("Node", child);
			pageContext.include(Registry.getView(child));
			request.setAttribute("Node", composite);
		} else {
%>
		<div class="Content <%=child.getType() %>">
			<a href="<%=request.getContextPath() %><%=child.getPath()%>" class="<%=child.getType() %>"><%=child.getTitle()%></a>
		</div>
<%
		}
	}
%>
</div>