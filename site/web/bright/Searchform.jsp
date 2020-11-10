<form method="get" class="searchform" action="<%=request.getAttribute("Page.Root") %>">
<p>
<input type="text" name="query" class="textbox" value="<%=request.getParameter("query") != null ? request.getParameter("query") : ""%>" />
<input type="hidden" name="page" value="/site/service/Search.jsp" />
<input type="submit" class="button" value="Search" />
</p>
</form>