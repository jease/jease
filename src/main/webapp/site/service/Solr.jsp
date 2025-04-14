<%@page import="jease.site.Solr"%>
<%@page import="org.apache.solr.client.solrj.response.FacetField"%>
<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.commons.lang3.StringEscapeUtils"%>
<%@page import="org.apache.solr.common.SolrDocument"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>
<%@page import="java.util.Map"%>
<style> em{ background-color: yellow; font-weight: bold; } </style>
<%
    Solr s = new Solr();
    String q = "", p = "", page2 = "", fq = "", sort = "";
    q = request.getParameter("query");
    p = request.getParameter("p");
    fq = request.getParameter("fq");
    page2 = request.getParameter("page");
    sort = request.getParameter("sort");
    if (null == q) {
        q = "";
    }
    if (null == fq) {
        fq = "";
    }
    if (null == p) {
        p = "0";
    }
    if (null == sort) {
        sort = "0";
    }
    if (null == page2) {
        page2 = "/site/service/search.jsp";
    }
    List<jease.site.Solr.items> result = s.getresult(q, p, fq, sort);
%>
<div class="row">
    <div class="col-md-8">
        <form class="form-group" action="<%=request.getContextPath()%>/" method="get">
            <div class="input-icon mb-3">
                <input type="search" name="query" <% if (request.getParameter("query") != null) {%>value="<%= StringEscapeUtils.escapeHtml4(request.getParameter("query"))%>"<% } else { %>value="Enter your search..." onfocus="this.value = '';"<% } %>  class="form-control" placeholder="Search for...">
                <input type="hidden" name="page" value="/site/service/Search.jsp" />
                <span class="input-icon-addon">
                    <i class="fe fe-search"></i>
                </span>
            </div>
        </form>
        <%
            for (jease.site.Solr.items item : result) {
                String url = item.url;
                if (null != url && url.startsWith("/")) {
                    url = url.substring(1);
                }
        %>
        <a href="<%=url%>"><%=item.title%></a><br/>
        <span ><%=item.snip%></span><br/>
        <span ><%=String.format("%tF", item.d)%></span><br/>
        <hr/>
        <%}%>
        <%@include file="/site/service/pager/SolrPagination.jspf" %>
    </div>
    <div class="col-md-4">
        <div class="card">
            <div class="container">
                <div class="card-header">
                    <h3 class="card-title">Filter your result:</h3>
                </div>
                <div class="container ">
                    <div class="dropdown">
                        <button type="button" class="btn btn-secondary btn-block dropdown-toggle" data-toggle="dropdown">
                            Sort
                        </button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=p%>&fq=<%=fq%>&sort=1">Based on Date (ASC)</a>
                            <a class="dropdown-item" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=p%>&fq=<%=fq%>&sort=0">Based on Date (DESC)</a>
                            <a class="dropdown-item" href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=p%>&fq=<%=fq%>&sort=2">Based on Title (ASC)</a>
                        </div>
                    </div>
                </div>
                <div class="list-group list-group-transparent mb-0">
                    <%

                        for (FacetField ff : s.fflist) {
                            String ffname = ff.getName();
                            int ffcount = ff.getValueCount();
                    %>
                    <div class="card-header">
                        <h3 class="card-title"><%=ffname%></h3>
                    </div>
                    <%
                        List<FacetField.Count> counts = ff.getValues();
                        for (FacetField.Count c : counts) {
                            String facetLabel = c.getName();
                            long facetCount = c.getCount();
                            if (facetCount == 0) {
                                continue;
                            }
                    %>
                    <div class="list-group-item list-group-item-action d-flex align-items-center ">
                        <a href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=p%>&fq=<%=fq%><%=ffname%>:<%=facetLabel%>&sort=0" class="tag tag-blue">
                            <%=facetLabel%>(<%=facetCount%>)
                        </a></div>
                        <%}
                            }%>
                    <div class="list-group-item list-group-item-action d-flex align-items-center ">
                        <a href="<%=request.getContextPath()%>/?query=&page=<%=page2%>" class="btn btn-danger">Remove all fillter</a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>