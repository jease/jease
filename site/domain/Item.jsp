<%@page import="java.awt.*,org.apache.commons.io.FileUtils,jfix.util.*,jfix.servlet.*,jease.cms.domain.*,jease.cms.domain.property.*"%>
<% 
	Item item = (Item) request.getAttribute("Node");
	if (session.getAttribute(item.getPath()) != null) {
		item = (Item) session.getAttribute(item.getPath());
	}
	
	if (request.getParameter("file") != null) {
		FileProperty file = (FileProperty) item.getProperty(request.getParameter("file"));
		if (file.getContentType().startsWith("image") && request.getParameter("scale") != null) {
			int scale = Integer.parseInt(request.getParameter("scale"));
			Servlets.write(Images.scale(file.getFile(), scale), file.getContentType(), response);			
		} else {
		 	Servlets.write(file.getFile(), file.getContentType(), response);		 
		}
		return;
	}
%>
<div class="Item">
<h1 class="Title"><%=item.getTitle()%></h1>
<dl>
<% for (Property property : item.getProperties()) { %>
	<dt class="Name"><%=I18N.get(property.getName()) %></dt>
	<dd class="Property">
	<% if (property instanceof FileProperty) { %>	
		<%  if(((FileProperty) property).getContentType().startsWith("image")) { %>
			<a class="Image" href="?file=<%= property.getName() %>&<%=((FileProperty) property).getContentType().replace("/",".") %>"><img src="?file=<%= property.getName() %>" /></a>
		<% } else { %>
			<a class="<%=property.getType() %>" href="?file=<%= property.getName()%>"><%=property.toString()%></a>
		<% } %>
	<% } else { %>
		<%=String.valueOf(property).replace("\n","<br />")%>
	<% } %>		
	</dd>
<% } %>
</dl>
</div>