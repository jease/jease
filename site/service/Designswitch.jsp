<% if (!jease.Registry.getParameter(jease.Names.JEASE_SITE_DESIGN,"").startsWith("/")) { %>
Design
<% for (String design : new String[] { "bright", "cool", "loop", "photo", "robot", "simple" }) { %>
 | <a href="?design=<%=design%>"><%=design%></a>
<% } %>
<% } %>