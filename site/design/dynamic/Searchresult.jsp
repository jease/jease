<%@page import="jease.cmf.domain.*,jease.cms.domain.*,jease.site.service.FullTexts"%>
<%
	for (Content content : FullTexts.query(request.getParameter("query"))) {
%>
    <p>
<%
	for (Node parent : content.getParents()) {
%> 
        &raquo; <%=parent.getTitle()%> 
<%
 	}
 %>
        <br />
        <a href="<%=content.getPath()%>"><%=content.getTitle()%></a>
    </p>
<%
	}
%>