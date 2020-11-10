<%@page import="jease.cms.domain.*"%>
<%
	Topic topic = (Topic) request.getAttribute("Node");
%>
<div class="Topic">
<h1><%= topic.getTitle() %></h1>
</div>