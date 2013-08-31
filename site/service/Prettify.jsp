<%-- http://code.google.com/p/google-code-prettify/ --%>
<link rel="stylesheet" type="text/css" href="<%=request.getContextPath() %>/site/service/prettify/prettify.css" media="all"/>
<script type="text/javascript" src="<%=request.getContextPath() %>/site/service/prettify/prettify.js"></script> 
<script type="text/javascript">//<![CDATA[
$(document).ready(function(){	
	$("code,pre").addClass("prettyprint");
	prettyPrint();
});
//]]></script>