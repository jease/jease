<%@page import="jease.cmf.domain.*,jease.cms.domain.*"%>
<%
	Node node = (Node) request.getAttribute("Node");
%>
<h1><%=node.getParent().getTitle()%></h1>
<ul>
	<%
		for (Content content : ((Folder) node.getParent()).getVisibleChildren(Content.class)) {
			if (content instanceof News) {
				continue;
			}
			if (content instanceof Topic) {
	%>
</ul>
<h1><%=content.getTitle()%></h1>
<ul>
	<%
		} else {
	%>
	<li<%= content == node ? " class=\"current\"" : "" %>>
	   <a href="<%=content.getPath()%>" ><%=content.getTitle()%><%=content.isContainer() ? "..." : ""%></a>
	</li>
	<%
		}
		}
	%>
</ul>
