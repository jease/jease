<%@page import="jease.cms.domain.*,jease.site.*"%>
<ul>
	<%
		String contextPath = ((Content) request.getAttribute("Node")).getPath();
		for (Content content : Navigations.getTabs()) {
			String contentPath = content.getPath();
			boolean current = contextPath.startsWith(contentPath + "/");
	%>
	<li<%=current ? " class=\"current\"" : ""%>>
	 <a href="<%=contentPath%>"><span><%=content.getTitle()%></span></a>
	</li>
	<%
		}
	%>
</ul>
