<%@page import="jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.*"%>
<%
	Content navigationContext = (Content) request.getAttribute("Node");
%>
<h1><a href="<%=navigationContext.getParent().getPath()%>"><%=navigationContext.getParent().getTitle()%></a></h1>
<ul>
	<%
		for (Content content : Navigations.getItems((Content) navigationContext.getParent())) {
		if (content instanceof Topic) {
	%>
</ul>
<h1><%=content.getTitle()%></h1>
<ul>
	<%
		} else {
	%>
	<li <%=content == navigationContext ? " class=\"current\"" : ""%>>
		<a href="<%=content.getPath()%>"><%=content.getTitle()%></a>
	</li>
	<%
		}
		}
	%>
</ul>
