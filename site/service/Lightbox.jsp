<%-- http://www.no-margin-for-errors.com/projects/prettyphoto-jquery-lightbox-clone/  --%>
<link rel="stylesheet" href="<%= request.getContextPath() %>/site/service/lightbox/css/prettyPhoto.css" type="text/css" media="screen" />
<style type="text/css">.currentTextHolder {font-size: 12px;}</style>
<script type="text/javascript" src="<%= request.getContextPath() %>/site/service/lightbox/js/jquery.prettyPhoto.js"></script>
<script type="text/javascript">//<![CDATA[                                          
$(document).ready(function(){
	var config = {
			show_title: true,
			deeplinking: false,
			default_width: 640,
			default_height: 480,
			overlay_gallery: true, 
			hideflash: true,
			social_tools: ''
	};
	$("object,embed").attr({"width": 600, "height": 400});
	$("a.Image").attr({"rel" : "prettyPhoto[image]"}).prettyPhoto(config);
	$("a[href$=.jpg] img,a[href$=.gif] img,a[href$=.png] img").parent().attr({"rel" : "prettyPhoto[image]"}).prettyPhoto(config);
	$("a[href$=.mov]").attr({"rel" : "prettyPhoto[movie]"}).prettyPhoto(config);
	$("a[href$=.flv]").attr({"rel" : "prettyPhoto[movie]"}).each(function() { this.href += "?print&iframe=true";}).prettyPhoto(config);
	$("a[href$=.swf]").attr({"rel" : "prettyPhoto[flash]"}).each(function() { this.href += "?width=600&height=400"; }).prettyPhoto(config);
	$("a[href$=?print]").attr({"rel" : "prettyPhoto[iframe]"}).each(function() { this.href += "&iframe=true";}).prettyPhoto(config);
	<% if (request.getParameter("print") != null) { %>
		$("a").click(function(evt) {
			if(!this.target) {
				window.parent.$.prettyPhoto.close();
				window.parent.location = this.href;
			}
		});
	<% } %>
});
//]]></script>
