<%@page import="jfix.util.I18N"%>
<%@page import="jfix.servlet.Servlets"%>
<%@page import="jease.cms.domain.Content"%>
<a href="http://do.convertapi.com/Web2Pdf?PrintType=true&OutputFileName=<%=((Content) request.getAttribute("Node")).getId() %>.pdf&CUrl=<%= Servlets.getContextURL(request) %><%=((Content) request.getAttribute("Node")).getPath() %>" rel="noindex, nofollow" target="_blank"><%=I18N.get("Page_as_PDF")%></a>