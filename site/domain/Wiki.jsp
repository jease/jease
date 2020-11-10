<%@page import="jease.cms.domain.*,org.eclipse.mylyn.wikitext.core.parser.*,org.eclipse.mylyn.wikitext.core.parser.builder.*,org.eclipse.mylyn.wikitext.mediawiki.core.*"%>
<%
	Wiki wiki = (Wiki) request.getAttribute("Node");
%>
<h1><%=wiki.getTitle()%></h1>
<%
	MarkupParser parser = new MarkupParser(new MediaWikiLanguage(), new HtmlDocumentBuilder(out));
	parser.parse(wiki.getContent(), false);
%>
