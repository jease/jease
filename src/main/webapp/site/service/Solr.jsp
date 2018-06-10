<%@page import="jease.site.Solr"%>
<%@page import="org.apache.solr.client.solrj.response.FacetField"%>
<%@page import="org.apache.solr.client.solrj.response.QueryResponse"%>
<%@page import="java.util.ArrayList"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.apache.solr.common.SolrDocument"%>
<%@page import="org.apache.solr.common.SolrDocumentList"%>

<%@page import="java.util.Map"%>

<%
    Solr s = new Solr();
    String q = "", p = "", page2 = "", fq = "";
    q = request.getParameter("query");
    p = request.getParameter("p");
    fq = request.getParameter("fq");
    page2 = request.getParameter("page");
    List<jease.site.Solr.items> result = s.getresult(q, p, fq);
//QueryResponse response=s.getresult(q,p);
%>
<div class="row">

    <div class="col-md-8">

        <div class="form-group">
            <label class="form-label">Search in all content on jease!</label>
            <div class="input-icon mb-3">
                <input type="text" class="form-control" placeholder="Search for...">
                <span class="input-icon-addon">
                    <i class="fe fe-search"></i>
                </span>
            </div>
        </div>
        <%
            for (jease.site.Solr.items item : result) {

        %>

        <a href="<%=request.getContextPath()%>/<%=item.url%>"><%=item.title%></a><br/>
        <span ><%=item.snip%></span><br/>
        <span ><%=String.format("%tF", item.d)%></span><br/>
        <hr/>
        <%}%>




    </div>
    <div class="col-md-4">
        <div class="card">
            <div class="card-header">
                <h3 class="card-title">Filter your result:</h3>
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
                    <a href="<%=request.getContextPath()%>/?query=<%=q%>&page=<%=page2%>&p=<%=p%>&fq=<%=ffname%>:<%=facetLabel%>" class="tag tag-blue">
                        <%=facetLabel%>(<%=facetCount%>)

                    </a></div>
                    <%
                            }
                        }
                    %>

            </div>
        </div>
    </div>
</div>