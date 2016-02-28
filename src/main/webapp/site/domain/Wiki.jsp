<%@page import="jease.Names"%>
<%@page import="jease.Registry"%>
<%@page import="jease.cms.domain.Wiki"%>
<%@page import="jease.site.Markups"%>
<%
	Wiki wiki = (Wiki) request.getAttribute("Node");
	if (session.getAttribute(wiki.getPath()) != null) {
		wiki = (Wiki) session.getAttribute(wiki.getPath());
	}
%>
<div class="Wiki">
<h1 class="Title"><%=wiki.getTitle()%></h1>
<div class="Content">
	<%= Markups.render(wiki.getContent(),
				Registry.getParameter(Names.JEASE_WIKI_LANGUAGE, Markups.MEDIA_WIKI),
				Registry.getParameter(Names.JEASE_WIKI_LINKS, Markups.LINK_PATTERN)) %>
</div>
</div>