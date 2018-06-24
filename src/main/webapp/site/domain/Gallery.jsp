<%@page import="jease.cms.domain.Image"%>
<%@page import="jease.cms.domain.Gallery"%>
<%
	Gallery gallery = (Gallery) request.getAttribute("Node");
	if (session.getAttribute(gallery.getPath()) != null) {
		gallery = (Gallery) session.getAttribute(gallery.getPath());
	}
	int scale = gallery.getScale();
%>
<div class="Gallery">
<h1 class="Title"><%=gallery.getTitle() %></h1>
<div class="Preface"><%=gallery.getPreface() %></div>
<% for (Image image : ((Gallery) request.getAttribute("Node")).getChildren(Image.class)) { %>
	<a class="Image" href="<%=request.getContextPath() %><%=image.getPath()%>" style="<%= image.isVisible() ? "" : " display: none"%>">
		<% if (gallery.isLabeled()) { %>
			<span class="Label"><%=image.getTitle()%><br /></span>
		<% } %>
		<img src="<%=request.getContextPath() %><%=image.getPath()%>?scale=<%= scale %>"<% if (gallery.isLabeled()) { %> title="<%=image.getTitle()%>" alt="<%=image.getTitle()%>"<% } %> />
	</a>
<% } %>
</div>