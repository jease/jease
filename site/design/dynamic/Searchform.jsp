<form method="get" class="searchform" action="<%=request.getAttribute("Page.Path") %>">
<p>
<input type="text" name="query" class="textbox" 
          value="<%=request.getParameter("query") != null ? request.getParameter("query") : ""%>" />
<input type="submit" class="button" value="Search" />
</p>
</form>