    package jfix.db4o.engine.migration;
import org.jease.cms.domain.Folder;
import jfix.db4o.Database;
import org.jease.cms.domain.Content;
import org.jease.cmf.service.Nodes;
import java.util.*;
import java.lang.Exception;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;
import java.text.SimpleDateFormat;
import java.util.stream.Stream;

/**
 * Created by mnm
 * User: mnm
 * Date: 6/10/18
 * Time: 18:43
 * http://github.com/ghaseminya
 */
public class AnytoSolr {


    String solrurl = "";//jease.Registry.getParameter(jease.Names.JEASE_SOLR_URL, "");
    int count=0;

    public static void main(String a[]){
        AnytoSolr d=new AnytoSolr();
        d.initDatabase();
        d.start(a[0]);
    }
    public void start(String solr){
        solrurl=solr;
        Content root = (Content)Nodes.getRoot();
        recursiveloop(root);
    }
    protected void initDatabase() {
        String engine = "jfix.db4o.engine.PersistenceEnginePerst";//for other engine must be change
        if (engine != null && !engine.isEmpty()) {
            Database.setPersistenceEngine(engine);
        }
        String databaseName = "jease";
        if (databaseName != null) {
            Database.open(databaseName);
        } else {
            throw new RuntimeException();
        }
    }
    public void recursiveloop(Content root){

        for (Content c : Stream.of(
                root.getChildren(Content.class))
                .filter(content ->  content.isVisible())
                .toArray(Content[]::new)) {
                System.out.println("item "+count++);
                insertToSolr(c.getId(),c.getPath(),c.getTitle(),c.getEditor().getName(),c.getFulltext().toString(),c.getType(),c.getLastModified(),c.getParent().getId(),"");
                if(c instanceof Folder )
                    recursiveloop(c);
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
