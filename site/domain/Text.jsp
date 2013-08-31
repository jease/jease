<%@page import="jease.cms.domain.Text"%>
<%
	Text text = (Text) request.getAttribute("Node");
	if (session.getAttribute(text.getPath()) != null) {
		text = (Text) session.getAttribute(text.getPath());
	}
%>
<div class="Text">
<h1 class="Title"><%=text.getTitle()%></h1>
<div class="Content"><%=text.getContent()%></div>
</div>