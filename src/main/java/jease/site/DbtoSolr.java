package jease.site;
import jfix.db4o.Database;
import jease.cms.domain.Content;
import jease.cmf.service.Nodes;
import java.util.*;
import java.lang.Exception;
import jease.Names;
import jease.Registry;
import jease.site.Navigations;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import java.text.SimpleDateFormat;
/**
 * Created by mnm
 * User: mnm
 * Date: 6/10/18
 * Time: 18:43
 * http://github.com/ghaseminya
 */
public class DbtoSolr {


    String solrurl = jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
    public void start(){
        Content root = (Content)Nodes.getRoot();
        for (Content tab1 : Navigations.getTabs(root)) {
            for (Content c : Navigations.getItems(tab1)) {
                insertToSolr(c.getId(),c.getPath(),c.getTitle(),c.getEditor().getName(),c.getFulltext().toString(),c.getTages(),c.getLastModified(),"","");
            }
//alert("finish"+tab1.getTitle());
        }
    }
    public void insertToSolr(String id,String path,String title,String author,String text,String type,Date lastmodif,String categ,String tags){
        if(solrurl.equals(""))return;
        SimpleDateFormat month_date = new SimpleDateFormat("MMM yyyy", Locale.ENGLISH);
        try {
            ArrayList tagslist = new ArrayList(Arrays.asList(tags.split(",")));
            SolrClient client = new HttpSolrClient.Builder(solrurl).build();
            SolrInputDocument doc = new SolrInputDocument();
            doc.addField("id", UUID.randomUUID().toString());
            doc.addField("tags",  tagslist);
            doc.addField("jeaseid",  id);
            doc.addField("jeasepath", path);
            doc.addField("title", title);
            doc.addField("author", author);
            doc.addField("type", type);
            doc.addField("text", text);
            doc.addField("last_modified", lastmodif);
            doc.addField("date", month_date.format(lastmodif));
            doc.addField("category",categ );
            client.add(doc);
            client.commit();
        }catch (Exception s){
            s.printStackTrace();
        }
    }
}
