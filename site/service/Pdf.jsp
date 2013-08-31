<%@page import="java.net.URL"%>
<%@page import="jfix.util.I18N"%>
<%@page import="jfix.html.Html2Pdf"%>
<%@page import="jfix.servlet.Servlets"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.site.Navigations"%>
<%
	String pdfPath = request.getParameter("path");
	if (pdfPath != null) {
		Content pdfContent = (Content) Navigations.getRoot().getChild(pdfPath);
		URL pdfUrl = new URL(request.getScheme() + "://" + Servlets.getHost(request) 
								+ request.getContextPath() + Navigations.getBasePath(pdfContent));
		byte[] pdfBytes = new Html2Pdf(pdfUrl).getBytes();
		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition","inline; filename=" + pdfContent.getId() + ".pdf"); 
		response.setContentLength(pdfBytes.length);
		response.getOutputStream().write(pdfBytes);
		return;
	} else {
%>
<a href="<%=request.getContextPath()%>/site/service/Pdf.jsp?path=<%=((Content) request.getAttribute("Node")).getPath() %>" rel="noindex, nofollow" target="_blank"><%=I18N.get("Page_as_PDF")%></a>
<% } %>