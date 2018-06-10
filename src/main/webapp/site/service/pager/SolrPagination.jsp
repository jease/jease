<div>
    <ul class="pagination ">
        <li class="page-item page-prev <%if (!s.hasprev) {%>disabled<%}%>">
            <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start - 1)%>&fq=<%=fq%>&sort=<%=sort%>" tabindex="-1">
                Prev
            </a>
        </li>
        <%for (int i = 0; i < (s.total / 10) + 1; i++) {%>
        <li class="page-item <%if (i == s.start) {%>disabled<%}%>"><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=i%>&fq=<%=fq%>&sort=<%=sort%>"><%=i + 1%></a></li>
            <%}%>
        <li class="page-item page-next <%if (!s.hasnext) {%>disabled<%}%>">
            <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start + 1)%>&fq=<%=fq%>&sort=<%=sort%>">
                Next
            </a>
        </li>
        <%
        %>
    </ul>
</div>