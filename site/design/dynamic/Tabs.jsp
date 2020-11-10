<%@page import="jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<ul>
	<%
		String nodePath = ((Node) request.getAttribute("Node")).getPath();
		for (Folder folder : Navigations.getTabs()) {
			String folderPath = folder.getPath();
			boolean current = nodePath.startsWith(folderPath + "/");
	%>
	<li<%=current ? " class=\"current\"" : ""%>>
	 <a href="<%=folderPath%>"><span><%=folder.getTitle()%></span></a>
	</li>
	<%
		}
	%>
</ul>
