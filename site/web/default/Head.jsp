<script type="text/javascript">//<![CDATA[
document.write("<base href=\"" + window.location.protocol + "//" + window.location.host + "<%=request.getAttribute("Page.Base") %>\" />");
//]]></script>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/default/style/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/web/default/style/print.css" media="<%= request.getParameter("print") == null ? "print" : "print,screen" %>" />
<%@include file="/site/service/Topup.jsp"%>
<%@include file="/site/service/Prettify.jsp"%>