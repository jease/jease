<%@page import="jease.cms.domain.*,jease.site.*"%>
<%
	Wiki wiki = (Wiki) request.getAttribute("Node");
%>
<div class="Wiki">
<h1><%=wiki.getTitle()%></h1>
<div><%= Markups.renderMediaWiki(wiki.getContent()) %></div>
</div>
