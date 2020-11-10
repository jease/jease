<%
	String designsPath = application.getRealPath("/site/web/");
	// Path could be null in unexploded WAR
	if (designsPath != null) {	
		String[] designs = jfix.util.Natural.sort(new java.io.File(designsPath).list());
%>
	Design
	<% for (String design : designs) { %>
		| <a href="?design=<%=design%>"><%=design%></a>
	<% } %>
<% } %>