<%@page contentType="text/html; charset=ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=iso-8859-1" />
<title><%= ((Node) request.getAttribute("Node")).getTitle() %></title>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Path") %>site/design/static/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Path") %>site/design/static/print.css" media="print" />
</head>
<body>
<div id="wrap">
    <div id="header">
        <%@include file="Logo.jsp"%>
        <%@include file="Searchform.jsp"%>
        <div id="tabs">
            <%@include file="Tabs.jsp"%>
        </div>
    </div>
    <div id="content-wrap">
        <div id="photo"></div>
        <div id="leftbar">
            <%@include file="Navigation.jsp"%>
        </div>
        <div id="main">      
            <div id="breadcrumb"><%@include file="Breadcrumb.jsp"%></div>      
            <% pageContext.include((String) request.getAttribute("Page.Template")); %>
            <div id="editorial"><%@include file="Editorial.jsp"%></div>
        </div>
        <div id="rightbar">
            <%@include file="Newslist.jsp"%>
        </div>
    </div>
    <div id="footer">
        <%@include file="Footer.jsp"%>
    </div>
</div>
</body>
</html>