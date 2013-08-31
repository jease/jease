<%@page import="org.apache.commons.lang3.ArrayUtils"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="jfix.functor.Functors"%>
<%@page import="jfix.functor.Predicate"%>
<%@page import="jfix.util.I18N"%>
<%@page import="jease.Names"%>
<%@page import="jease.cms.domain.Access"%>
<%@page import="jease.cms.domain.Content"%>
<%@page import="jease.cms.domain.User"%>
<%@page import="jease.site.Authorizations"%>
<%@page import="jease.site.Fulltexts"%>
<h1><%=I18N.get("Search_results_for")%> &quot;<%=StringEscapeUtils.escapeXml(request.getParameter("query"))%>&quot;</h1>
<%
	final Collection<Access> authorizations = (Collection<Access>) session.getAttribute(Names.JEASE_SITE_AUTHORIZATIONS);
	final User user = (User) session.getAttribute(User.class.toString());
	List<Content> contents = Fulltexts.query((Content) request.getAttribute("Root"), request.getParameter("query"));
	contents = Functors.filter(contents, new Predicate<Content>() {
		public boolean test(Content content) {
			Access[] guards = Authorizations.getGuards(content);
			if (ArrayUtils.isEmpty(guards)) {
				return true;
			}
			for (Access guard : guards) {
				if ((authorizations != null && authorizations.contains(guard)) 
						|| (user != null && guard.isDescendant(user.getRoots()))) {
					return true;
				}
			}
			return false;
		}
	});
	if (!contents.isEmpty()) {
		request.setAttribute("Pager.Scope", "search");
		request.setAttribute("Pager.Contents", contents);
		request.setAttribute("Pager.Renderer", "/site/service/pager/Searchresult.jsp");
		pageContext.include("/site/service/pager/Pager.jsp");
	} else {
%>
<p><%=I18N.get("No_results")%>.</p>
<% } %>