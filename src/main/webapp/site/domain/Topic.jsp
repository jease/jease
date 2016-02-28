<%@page import="jease.cms.domain.Topic"%>
<%
	Topic topic = (Topic) request.getAttribute("Node");
	if (session.getAttribute(topic.getPath()) != null) {
		topic = (Topic) session.getAttribute(topic.getPath());
	}
%>
<div class="Topic">
<h1 class="Title"><%= topic.getTitle() %></h1>
</div>