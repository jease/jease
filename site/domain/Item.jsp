<%@page import="jfix.util.I18N"%>
<%@page import="jfix.util.Images"%>
<%@page import="jease.cms.domain.Item"%>
<%@page import="jease.cms.domain.property.Property"%>
<%@page import="jease.cms.domain.property.HtmlProperty"%>
<%@page import="jease.cms.domain.property.FileProperty"%>
<%@page import="jease.site.Streams"%>
<%
	Item item = (Item) request.getAttribute("Node");
	if (session.getAttribute(item.getPath()) != null) {
		item = (Item) session.getAttribute(item.getPath());
	}
	
	if (request.getParameter("file") != null) {
		FileProperty file = (FileProperty) item.getProperty(request.getParameter("file"));
		Streams.write(request, response, file.getFile(), file.getContentType());
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
		<%  if (Images.isBrowserCompatible(((FileProperty) property).getContentType())) { %>
			<a class="Image" href="?file=<%= property.getName() %>&<%=((FileProperty) property).getContentType().replace("/",".") %>"><img src="?file=<%= property.getName() %>" /></a>
		<% } else { %>
			<a class="<%=property.getType() %>" href="?file=<%= property.getName()%>"><%=property.toString()%></a>
		<% } %>
	<% } else if (property instanceof HtmlProperty) { %>
		<%=String.valueOf(property)%>
	<% } else { %>
		<%=String.valueOf(property).replace("\n","<br />")%>
	<% } %>
	</dd>
<% } %>
</dl>
</div>