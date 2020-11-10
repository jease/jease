<%@page import="jease.cmf.service.*,jease.cms.domain.*" %>
<% String rootTitle = ((Content) Nodes.getRoot()).getTitle(); %>
<link rel="alternate" type="application/rss+xml"  href="<%=request.getAttribute("Page.Root") %>site/service/feeds/rss.jsp" title="<%=rootTitle%> RSS Feed" />
<link rel="alternate" type="application/atom+xml" href="<%=request.getAttribute("Page.Root") %>site/service/feeds/atom.jsp" title="<%=rootTitle%> ATOM Feed" />
