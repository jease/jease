<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.Composite"%>
<%@page import="jease.site.Templates"%>
<%@page import="jease.site.Navigations"%>
<%
	Composite composite = (Composite) request.getAttribute("Node");
%>
<div class="Composite">
<%
	for (Content child : Navigations.getVisibleContent(composite)) {
		if (child.isPage()) {
			request.setAttribute("Node", child);
			pageContext.include(Templates.get(child));
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