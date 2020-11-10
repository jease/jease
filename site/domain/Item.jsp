<%@page import="org.apache.commons.io.FileUtils,jfix.servlet.*,jease.cms.domain.*,jease.cms.domain.property.*"%>
<% 
	Item item = (Item) request.getAttribute("Node");

	if (request.getParameter("file") != null) {
	     FileProperty file = (FileProperty) item.getProperty(request.getParameter("file"));	     
		 Servlets.write(file.getFile(), file.getContentType(), response);
		 return;
	}
%>
<div class="Item">
<h1><%=item.getTitle()%></h1>
<dl>
<% for (Property property : item.getProperties()) { %>
	<dt><%=property.getName() %></dt>
	<dd>
	<% if (property instanceof FileProperty) { %>	
		<a href="?file=<%= property.getName()%>"><%=property.toString()%></a>
	<% } else { %>
		<%=property.toString()%>
	<% } %>		
	</dd>
<% } %>
</dl>
</div>