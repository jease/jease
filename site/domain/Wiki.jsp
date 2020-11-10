<%@page import="jease.cms.domain.*,jease.site.*"%>
<%
	Wiki wiki = (Wiki) request.getAttribute("Node");
%>
<h1><%=wiki.getTitle()%></h1>
<%= Markups.renderMediaWiki(wiki.getContent()) %>
