<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.*,jease.site.i18n.*"%>
<%! 
	final String JEASE_DISCUSSION_RECURSION = "Jease.Discussion.Recursion";
%>
<%  
	Discussion discussion = (Discussion) request.getAttribute("Node");
	String id = String.valueOf(discussion.getPath().hashCode());	
	
	// Is the template called recursively?
	boolean toplevel = request.getAttribute(JEASE_DISCUSSION_RECURSION) == null ;
	String captcha = request.getParameter("captcha" + id);
	String subject = request.getParameter("subject" + id);
	String author = request.getParameter("author" + id);
	String comment = request.getParameter("comment" + id);
	String message = null;

	if (toplevel && captcha != null) {
		if (Validations.equals(captcha, session.getAttribute("captcha" + id))) {
			message = Discussions.addComment(discussion, author, subject, comment, true);
			if (message == null) {				
				subject = author = comment = null;
			}
		} else {
			message = Strings.Code_is_not_correct;	
		}
	}
%>
<div class="Discussion">

<%-- Show comment --%>
<p>
<% if (toplevel) { %>
	<b><%=discussion.getTitle()%></b>
<% } else { %>	
	<a href="<%=discussion.getPath()%>"><%=discussion.getTitle()%></a>
<% } %>
	<% if (Validations.isNotEmpty(discussion.getAuthor())) {%>
		<%= Strings.By %><%= " " + discussion.getAuthor() + " " %>
		(<%=String.format("%1$td %1$tb %1$tY", discussion.getLastModified())%>)
	<% } %>	
	<br />
	<%=discussion.getComment().replace("\n","<br />")%>
</p>

<%-- Create threaded view of discussion via recursion --%>
<ul>
	<% for (Discussion child : discussion.getChildren(Discussion.class)) { %>
		<% if (child.isVisible()) { %>	
		<li>
		<%
			request.setAttribute(JEASE_DISCUSSION_RECURSION, true);
			request.setAttribute("Node", child);			
			pageContext.include(jease.Registry.getView(child));			
			request.setAttribute("Node", discussion);
			request.removeAttribute(JEASE_DISCUSSION_RECURSION);
		%>
		</li>
		<% } %>
	<% } %>
</ul>

<%-- Form with captcha to add a comment --%>
<% if (toplevel) { %>
	<a name="discussion<%= id %>"></a>	
	<form action="#discussion<%= id %>" method="post">
		<dl>
		<dt><%= Strings.Name %>:</dt>
		<dd><input type="text" name="author<%=id %>" maxlength="60" value="<%=author != null ? author : Strings.Anonymous %>"<%= author == null ? " onFocus=\"this.value=''\"" :"" %>/></dd>
		<dt><%= Strings.Subject %>:</dt>
		<dd><input type="text" name="subject<%=id %>" maxlength="60" value="<%=subject != null ? subject : "" %>"/></dd>			
		<dt><%= Strings.Comment %>:</dt>
		<dd><textarea name="comment<%=id %>" rows="10"><%=comment != null ? comment : "" %></textarea></dd>			
		<dt><%= Strings.Please_enter_the_code %>:</dt>
		<dd>
			<img src="/site/service/Captcha.jsp?id=captcha<%=id %>" /> 
			<br />
			<input type="text" name="captcha<%=id %>" />
		</dd>	
		</dl>
		<p>
		<% if (Validations.isNotEmpty(message)) { %>
			<b><%=message %>!</b>	
		<% } else if (captcha != null) { %>
			<i><%=Strings.Thank_you_for_your_comment %>.</i>
		<% } %>			
		<input type="submit" value="<%=Strings.Submit %>" />
		</p>					
	</form>
<% } %>
</div>