<%-- http://code.google.com/p/google-code-prettify/ --%>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/service/prettify/prettify.css" media="all"/>
<script type="text/javascript" src="<%=request.getAttribute("Page.Root") %>site/service/prettify/prettify.js"></script> 
<script type="text/javascript">//<![CDATA[
$(document).ready(function(){	
	$("code,pre").addClass("prettyprint");
	prettyPrint();
});
//]]></script>
