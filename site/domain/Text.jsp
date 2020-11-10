<%@page import="jease.cms.domain.*"%>
<%
	Text text = (Text) request.getAttribute("Node");
%>
<div class="Text">
<h1 class="Title"><%=text.getTitle()%></h1>
<div class="Content"><%=text.getContent()%></div>
</div>