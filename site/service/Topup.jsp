<%-- http://gettopup.com/ --%>
<script type="text/javascript" src="<%=request.getAttribute("Page.Root") %>site/service/topup/javascripts/top_up-min.js"></script>
<script type="text/javascript">
TopUp.images_path = "<%=request.getAttribute("Page.Root") %>site/service/topup/images/";
TopUp.players_path = "<%=request.getAttribute("Page.Root") %>site/service/topup/players/";
TopUp.addPresets({
	".imagePopup": { type: "image", group: "gallery", overlayClose: 1 }, 
	".iframePopup": { type: "iframe", width: 640, height: 400, overlayClose: 1 },
	".flashPopup": { type: "flash", width: 800, height: 600, overlayClose: 1 },
	".flashvideoPopup": { type: "flashvideo", width: 800, height: 600, overlayClose: 1 }
});
</script>
