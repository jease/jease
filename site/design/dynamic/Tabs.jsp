<%@page import="jease.cmf.domain.*,jease.cmf.service.*,jease.cms.domain.*"%>
<ul>
	<%
		String nodePath = ((Node) request.getAttribute("Node")).getPath();
		for (Folder folder : ((Folder) Nodes.getRoot()).getVisibleChildren(Folder.class)) {
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
