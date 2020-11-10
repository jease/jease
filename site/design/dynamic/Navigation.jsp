<%@page import="jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	Node navigationNode = (Node) request.getAttribute("Node");
%>
<h1><%=navigationNode.getParent().getTitle()%></h1>
<ul>
	<%
		for (Content content : Navigations.getItems((Folder) navigationNode.getParent())) {
		if (content instanceof Topic) {
	%>
</ul>
<h1><%=content.getTitle()%></h1>
<ul>
	<%
		} else {
	%>
	<li <%=content == navigationNode ? " class=\"current\"" : ""%>>
	<a href="<%=content.getPath()%>"><%=content.getTitle()%><%=content.isContainer() ? "..." : ""%></a>
	</li>
	<%
		}
		}
	%>
</ul>
