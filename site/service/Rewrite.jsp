<%-- Rewrites internal links to external representation with final context path --%>
<script type="text/javascript">//<![CDATA[
$(document).ready(function(){
	$("a[href^=\./\~]").each(function() { 
		this.href = "<%=request.getContextPath()%>" + this.href.substring(this.href.indexOf("~") + 1);
	});
});
//]]></script>