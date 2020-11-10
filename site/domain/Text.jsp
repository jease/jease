<%@page import="jease.cms.domain.*"%>
<%
	Text text = (Text) request.getAttribute("Node");
%>
<div class="Text">
<h1><%=text.getTitle()%></h1>
<div><%=text.getContent()%></div>
</div>