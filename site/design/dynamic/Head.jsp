<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title><%= request.getAttribute("Page.Title") %></title>

<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Path") %>site/design/static/screen.css" media="screen" />
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Path") %>site/design/static/print.css" media="print" />
<% if(request.getParameter("print") != null) { %>
<link rel="stylesheet" type="text/css" href="<%=request.getAttribute("Page.Path") %>site/design/static/print.css" media="screen" />
<% } %>

<script type="text/javascript" src="http://gettopup.com/releases/latest/top_up-min.js"></script>
<script type="text/javascript">
	TopUp.addPresets({
			".imagePopup": { type: "image", group: "gallery", overlayClose: 1 }, 
			".iframePopup": { type: "iframe", width: 640, height: 400, overlayClose: 1 },
			".flashPopup": { type: "flash", width: 800, height: 600, overlayClose: 1 },
			".flashvideoPopup": { type: "flashvideo", width: 800, height: 600, overlayClose: 1 }
	});
</script> 
