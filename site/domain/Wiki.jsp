<%@page import="jease.cms.domain.*,jease.site.*"%>
<%
	Wiki wiki = (Wiki) request.getAttribute("Node");
%>
<div class="Wiki">
<h1 class="Title"><%=wiki.getTitle()%></h1>
<div class="Content"><%= Markups.renderMediaWiki(wiki.getContent()) %></div>
</div>
