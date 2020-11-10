Design
<% for (String design : jfix.util.Natural.sort(new java.io.File(application.getRealPath("/site/web/")).list())) { %>
| <a href="?design=<%=design%>"><%=design%></a>
<% } %>
