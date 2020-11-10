<%
	String errorPage = jease.Registry.getParameter(jease.Names.JEASE_SITE_ERROR);
	if (errorPage != null) {
		pageContext.forward(errorPage);
		return;
	}
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<title>Error report</title>
</head>
<body>
<h1>HTTP Status <%=request.getAttribute("javax.servlet.error.status_code") %></h1>
<p>The requested page (<%= request.getAttribute("javax.servlet.error.request_uri") %>) is unavailable.</p>
</body>
</html>