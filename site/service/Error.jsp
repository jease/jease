<%@page import="jease.cms.domain.Content"%>
<%
	String errorPage = jease.Registry.getParameter(jease.Names.JEASE_SITE_ERROR);
	if (errorPage != null) {
		if (errorPage.startsWith("/")) {
			pageContext.forward(errorPage);
		} else {
			pageContext.forward(((Content) request.getAttribute("Root")).getPath() + "/" + errorPage);
		}
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Error report</title>
<style>
body {
	font: 20px Constantia, "Hoefler Text", "Adobe Caslon Pro", Baskerville, Georgia, Times, serif;
	color: #999;
	text-shadow: 2px 2px 2px rgba(200, 200, 200, 0.5);
	text-align: center;
}
h1 {
	font-size: 50px;
}
</style>
</head>
<body>
	<h1>HTTP Status <%=request.getAttribute("javax.servlet.error.status_code")%></h1>
	<p>
		The requested page (<%=request.getAttribute("javax.servlet.error.request_uri")%>)
		is unavailable.
	</p>
</body>
</html>