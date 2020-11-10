<%@page import="jease.cms.domain.*"%>
<% 
	Gallery gallery = (Gallery) request.getAttribute("Node");
    int scale = gallery.getScale();
%>
<div class="Gallery">
<h1 class="Title"><%=gallery.getTitle() %></h1>
<div class="Preface"><%=gallery.getPreface() %></div>
<% for (Image image : gallery.getChildren(Image.class)) { %> 
	<a class="Image" href="<%=image.getPath()%>" style="<%= image.isVisible() ? "" : " display: none"%>">
		<% if (gallery.isLabeled()) {%>			
			<span class="Label">
				<%=image.getTitle()%>
				<br />
			</span>
		<% } %>	
		<img src="<%=image.getPath()%>?scale=<%= scale %>"
				<% if (gallery.isLabeled()) { %> title="<%=image.getTitle()%>" alt="<%=image.getTitle()%>"<% } %>
		/>				
	</a>
<% } %>
</div>
