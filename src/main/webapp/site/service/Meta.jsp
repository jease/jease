<%@page import="java.util.Properties"%>
<%@page import="java.io.StringReader"%>
<%@page import="java.util.Enumeration"%>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, user-scalable=no, initial-scale=1.0, maximum-scale=1.0, minimum-scale=1.0">
<meta http-equiv="X-UA-Compatible" content="ie=edge">
<meta http-equiv="Content-Language" content="en" />
<meta name="msapplication-TileColor" content="#2d89ef">
<meta name="theme-color" content="#4188c9">
<meta name="apple-mobile-web-app-status-bar-style" content="black-translucent"/>
<meta name="apple-mobile-web-app-capable" content="yes">
<meta name="mobile-web-app-capable" content="yes">
<meta name="HandheldFriendly" content="True">
<meta name="MobileOptimized" content="320">
<%
String meta=jease.Registry.getParameter(jease.Names.JEASE_SITE_META,"description \nauthor \nkeywords ");
Properties properties = new Properties();
properties.load(new StringReader(meta));
Enumeration e=properties.propertyNames();
		while (e.hasMoreElements()) {
			String key = (String) e.nextElement();
			String value=properties.getProperty(key);
            %>
            <meta name="<%=key%>" content="<%=value%>">
            <%
		}
%>