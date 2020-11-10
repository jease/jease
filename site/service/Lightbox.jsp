<%-- http://www.no-margin-for-errors.com/projects/prettyphoto-jquery-lightbox-clone/  --%>
<link rel="stylesheet" href="<%=request.getAttribute("Page.Root") %>site/service/lightbox/css/prettyPhoto.css" type="text/css" media="screen" />
<style type="text/css">.currentTextHolder {font-size: 12px;}</style>
<script type="text/javascript" src="<%=request.getAttribute("Page.Root") %>site/service/lightbox/js/jquery.prettyPhoto.js"></script>
<script type="text/javascript">//<![CDATA[                                          
$(document).ready(function(){
	var config = {
			show_title: false,
			theme: "light_rounded", 
			default_width: 640,
			default_height: 480,
			overlay_gallery: false, 
			hideflash: true
	};
	$("object,embed").attr({"width": 600, "height": 400});
	$("a img").parent().attr({"rel" : "prettyPhoto[image]"}).prettyPhoto(config);
	$("a[href$=.jpg],a[href$=.gif],a[href$=.png]").attr({"rel" : "prettyPhoto[image]"}).prettyPhoto(config);
	$("a[href$=.mov]").attr({"rel" : "prettyPhoto[movie]"}).prettyPhoto(config);
	$("a[href$=.flv]").attr({"rel" : "prettyPhoto[movie]"}).each(function() { this.href += "?print&iframe=true";}).prettyPhoto(config);
	$("a[href$=.swf]").attr({"rel" : "prettyPhoto[flash]"}).each(function() { this.href += "?width=600&height=400"; }).prettyPhoto(config);
	$("a[href$=?print]").attr({"rel" : "prettyPhoto[iframe]"}).each(function() { this.href += "&iframe=true";}).prettyPhoto(config);
});
//]]></script>
