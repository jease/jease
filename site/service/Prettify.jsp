<%-- http://code.google.com/p/google-code-prettify/ --%>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Root") %>site/service/prettify/prettify.css" media="all"/>
<script type="text/javascript" src="<%=request.getAttribute("Page.Root") %>site/service/prettify/prettify.js"></script> 
<script type="text/javascript">//<![CDATA[
(function() {
	var onload = window.onload;
	window.onload = function() {
		if (typeof onload == "function") {
			onload();
		}
		var applyPrettyPrint = function(tag) {
			var tags = document.getElementsByTagName(tag);
			for(var i=0; i < tags.length; i++) {
				if(!tags[i].className) {
					tags[i].className = "prettyprint";
				}
			}
		};
		applyPrettyPrint("code");
		applyPrettyPrint("pre");
		prettyPrint();
	}
}());
//]]></script>
