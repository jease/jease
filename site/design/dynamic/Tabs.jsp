<%@page import="jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<ul>
	<%
		String contextPath = ((Node) request.getAttribute("Node")).getPath();
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
