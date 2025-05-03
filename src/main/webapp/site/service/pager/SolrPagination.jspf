<div>
    <ul class="pagination ">
        <li class="page-item page-prev <%if (!s.hasprev) {%>disabled<%}%>">
            <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start - 1)%>&fq=<%=fq%>&sort=<%=sort%>" tabindex="-1">
                Prev
            </a>
        </li>
        <%
            if ((s.total / 10) > 5) {
                if (s.start - 2 > 0) {
        %>

        <li class="page-item "><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start - 2)%>&fq=<%=fq%>&sort=<%=sort%>"><%=s.start - 1%></a></li>
            <%}
                if (s.start - 1 > 0) {
            %>
        <li class="page-item "><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start - 1)%>&fq=<%=fq%>&sort=<%=sort%>"><%=s.start%></a></li>
            <%}%>
        <li class="page-item disabled"><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start)%>&fq=<%=fq%>&sort=<%=sort%>"><%=s.start + 1%></a></li>
            <%
                if (s.start + 1 < s.total) {
            %>

        <li class="page-item "><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start + 1)%>&fq=<%=fq%>&sort=<%=sort%>"><%=s.start + 2%></a></li>
            <%}
                if (s.start + 2 < s.total) {
            %>
        <li class="page-item "><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start + 2)%>&fq=<%=fq%>&sort=<%=sort%>"><%=s.start + 3%></a></li>
            <%}
        } else {
            for (int i = 0; i < (s.total / 10) + 1; i++) {%>
        <li class="page-item <%if (i == s.start) {%>active<%}%>"><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=i%>&fq=<%=fq%>&sort=<%=sort%>"><%=i + 1%></a></li>
            <%}
                }%>
        <li class="page-item page-next <%if (!s.hasnext) {%>disabled<%}%>">
            <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start + 1)%>&fq=<%=fq%>&sort=<%=sort%>">
                Next
            </a>
        </li>
    </ul>
</div>