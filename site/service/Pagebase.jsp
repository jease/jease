<%@page import="jease.cms.domain.*,jease.site.*"%>
<script type="text/javascript">//<![CDATA[
document.write("<base href=\"" + window.location.protocol + "//" + window.location.host + "<%= request.getContextPath() %><%=Navigations.getBasePath((Content) request.getAttribute("Node")) %>\" />");
//]]></script>
