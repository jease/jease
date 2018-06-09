package jease.site;
/**
 * Created by mnm
 * User: mnm
 * Date: 6/9/18
 * Time: 18:17
 * http://github.com/ghaseminya
 */
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Solr {
    public String SOLR_URL;
    private int pagesize=10;
    public boolean hasnext,hasprev;
    public List<items> getresult(String q,String p) {
        List<items> result = new ArrayList<>();
        try {
            SolrClient client = new HttpSolrClient.Builder(SOLR_URL).build();
            SolrQuery query = new SolrQuery();
            if (q.length() == 0) {
                q = "*:*";
            }
            query.setQuery(q);
            query.setFields("tags", "title", "author", "text", "last_modified", "type","category");
            try {
                if (p.equals(null)) {
                    p = "0";
                }
            } catch (Exception s) {
                p = "0";
            }
            int start = Integer.parseInt(p);
            query.setStart(start);
            query.setHighlight(true);
            query.addHighlightField("title,maincontent,tags");
//          query.setHighlightSimplePost("</b>");
//          query.setHighlightSimplePre("<b>");
//          query.setSort("last_modified", SolrQuery.ORDER.desc);
            query.set("defType", "edismax");
            QueryResponse response = client.query(query);
            SolrDocumentList results = response.getResults();
            long total = results.getNumFound();
            if (start * pagesize + pagesize < total) {
                hasnext = true;
            }
            if (start * pagesize + pagesize >= total) {
                hasnext = false;
            }
            if (start > 0) {
                hasprev = true;
            }
            if (start == 0) {
                hasprev = false;
            }
            long time = response.getQTime();
            Map<String, Map<String, List<String>>> hitHighlightedMap = response.getHighlighting();

            for (int i = 0; i < results.size(); ++i) {
                SolrDocument rs = results.get(i);
                System.out.println();
                items bi = new items();
                String main = "", tag = "", title = "";
                Map<String, List<String>> highlightedFieldMap = hitHighlightedMap.get(rs.getFieldValue("id"));
                if (highlightedFieldMap != null) {
                    List<String> highlightedList = highlightedFieldMap.get("title");
                    if (highlightedList != null) {
                        title = highlightedList.get(0);
                    }
                    highlightedList = highlightedFieldMap.get("tags");
                    if (highlightedList != null) {
                        tag = highlightedList.get(0);
                    }
                    highlightedList = highlightedFieldMap.get("text");
                    if (highlightedList != null) {
                        main = highlightedList.get(0);
                    }
                }
                bi.url = rs.getFieldValue("id") + "";
                bi.snip = rs.getFieldValue("author") + "";
                bi.d= (Date) rs.getFieldValue("last_modified");

                if (title.length() != 0) {
                    bi.title = title;
                } else {
                    bi.title = rs.getFieldValue("title") + "";
                }

                result.add(bi);
            }
        } catch (Exception s) {
            s.printStackTrace();
        }
        System.out.println("size of output" + result.size());
        return result;
    }
    public class items{
        public String title;
        public String snip;
        public String url;
        public Date d;
    }
}
