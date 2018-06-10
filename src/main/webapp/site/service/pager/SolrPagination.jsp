<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8" trimDirectiveWhitespaces="true"%>
<div>
              <ul class="pagination  pagination-pager">

                <%
                if(s.hasprev){
                %>
                <li class="page-item page-prev disabled">
                <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start-1)%>&fq=<%=fq%>&sort=<%=sort%>" tabindex="-1">
                                    Prev
                                  </a>
                                  </li>
                <%
                }
                for(int i=0;i<s.total/10;i++){
                %>
                <li class="page-item"><a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(i-1)%>&fq=<%=fq%>&sort=<%=sort%>"><%=i%></a></li>
                <%
                }
                if(s.hasnext){
                %>
                <li class="page-item page-next">
                                  <a class="page-link" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=(s.start+1)%>&fq=<%=fq%>&sort=<%=sort%>">
                                    Next
                                  </a>
                                </li>
                <%
                }
                %>


              </ul>
            </div>