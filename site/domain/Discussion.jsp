<%@page import="jfix.util.*,jease.cms.domain.*,jease.site.*,jease.*"%>
<%! 
	final String JEASE_DISCUSSION_RECURSION = "Jease.Discussion.Recursion";
%>
<%  
	Discussion discussion = (Discussion) request.getAttribute("Node");
	String id = String.valueOf(discussion.getPath().hashCode());
	if (session.getAttribute(discussion.getPath()) != null) {
		discussion = (Discussion) session.getAttribute(discussion.getPath());
	}
	
	// Is the template called recursively?
	boolean toplevel = request.getAttribute(JEASE_DISCUSSION_RECURSION) == null ;
	String captcha = request.getParameter("captcha" + id);
	String subject = request.getParameter("subject" + id);
	String author = request.getParameter("author" + id);
	String comment = request.getParameter("comment" + id);
	String message = null;

	if (toplevel && captcha != null) {
		if (Validations.equals(captcha, session.getAttribute("captcha" + id))) {
			message = Discussions.addComment((Discussion) request.getAttribute("Node"), author, subject, comment, true);
			if (message == null) {				
				subject = author = comment = null;
			}
		} else {
			message = I18N.get("Code_is_not_correct");	
		}
	}
%>
<div class="Discussion">

<%-- Show comment --%>
<div class="Comment">
	<span class="Title">
		<% if (toplevel) { %>
			<strong><%=discussion.getTitle()%></strong>
		<% } else { %>
			<% if (Registry.getParameter(Names.JEASE_DISCUSSION_PRESENTATION, "").toLowerCase().startsWith("thread")) { %>
				<a href="<%=request.getContextPath() %><%=((Discussion) request.getAttribute("Node")).getPath()%>"><%=discussion.getTitle()%></a>
			<% } else { %>
				<strong><%=discussion.getTitle()%></strong>
			<% } %>
		<% } %>
	</span>
	<% if (Validations.isNotEmpty(discussion.getAuthor())) {%>
		<span class="Author"><%= I18N.get("By") %><%= " " + discussion.getAuthor() + " " %></span>
		<span class="Date">(<%=String.format("%1$td %1$tb %1$tY", discussion.getLastModified())%>)</span>
	<% } %>	
	<div class="Text"><%=Regexps.convertTextToHtml(discussion.getComment())%></div>
</div>

<%-- Create threaded view of discussion via recursion --%>
<ul class="Thread">
	<% for (Discussion child : ((Discussion) request.getAttribute("Node")).getChildren(Discussion.class)) { %>
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
	<form class="Submission" action="#discussion<%= id %>" method="post">
		<dl>
		<dt><%= I18N.get("Name") %>:</dt>
		<dd><input type="text" name="author<%=id %>" maxlength="60" value="<%=author != null ? author : I18N.get("Anonymous") %>"<%= author == null ? " onFocus=\"this.value=''\"" :"" %>/></dd>
		<dt><%= I18N.get("Subject") %>:</dt>
		<dd><input type="text" name="subject<%=id %>" maxlength="60" value="<%=subject != null ? subject : "" %>"/></dd>			
		<dt><%= I18N.get("Comment") %>:</dt>
		<dd><textarea name="comment<%=id %>" rows="10"><%=comment != null ? comment : "" %></textarea></dd>			
		<dt><%= I18N.get("Please_enter_the_code") %>:</dt>
		<dd>
			<img src="<%=request.getContextPath() %>/site/service/Captcha.jsp?id=captcha<%=id %>" /> 
			<br />
			<input type="text" name="captcha<%=id %>" />
		</dd>	
		</dl>
		<p>
		<% if (Validations.isNotEmpty(message)) { %>
			<b><%=message %>!</b>	
		<% } else if (captcha != null) { %>
			<i><%=I18N.get("Thank_you_for_your_comment") %>.</i>
		<% } %>			
		<input type="submit" value="<%=I18N.get("Submit") %>" />
		</p>					
	</form>
<% } %>
</div>