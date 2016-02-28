/*
    Copyright (C) 2016 maik.jablonski@jease.org

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package jfix.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.miscellaneous.LimitTokenCountAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;

import java.util.ArrayList;
import java.util.List;

public class FullTextIndex<E> {

    // + - && || ! ( ) { } [ ] ^ " ~ * ? : \
    private static final String[] STRINGS_TO_QUOTE = new String[]{"-", "&", "!", "{", "}", "[", "]", ":", "?"};

    static {
        BooleanQuery.setMaxClauseCount(BooleanQuery.getMaxClauseCount() * 10);
    }

    private List<E> objects;
    private Directory indexDirectory;
    private IndexWriter indexWriter;
    private QueryParser queryParser;
    private Document document;
    private Field fulltext;

    public FullTextIndex() {
        try {
            objects = new ArrayList<>();

            Analyzer analyzer = new StandardAnalyzer();
            IndexWriterConfig config =  new IndexWriterConfig(new LimitTokenCountAnalyzer(analyzer, Integer.MAX_VALUE));

            indexDirectory = new RAMDirectory();
            indexWriter = new IndexWriter(indexDirectory, config);

            queryParser = new QueryParser("text", analyzer);
            queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);

            fulltext = new TextField("text", "", Field.Store.NO);

            // Used as base-set for a NOT-Query
            Field inverse = new TextField("true", "yes", Field.Store.NO);

            document = new Document();
            document.add(fulltext);
            document.add(inverse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void add(E object, String text) {
        try {
            if (object != null && text != null) {
                objects.add(object);
                fulltext.setStringValue(appendWithoutPunctuation(text));
                indexWriter.addDocument(document);
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public void commit() {
        try {
            indexWriter.commit();
            indexWriter.close();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public List<E> search(String search) throws Exception {
        try {
            ObjectCollector<E> collector = new ObjectCollector<>(objects);
            String query = buildQueryString(search);
            IndexReader indexReader = DirectoryReader.open(indexDirectory);
            IndexSearcher indexSearcher = new IndexSearcher(indexReader);
            indexSearcher.search(queryParser.parse(query), collector);
            indexReader.close();
            return collector.getOutput();
        } catch (Exception e) {
            throw new Exception("Query Syntax Error: " + search);
        }
    }

    protected String buildQueryString(String search) {
        for (String stringToQuote : STRINGS_TO_QUOTE) {
            search = search.replace(stringToQuote, "\\" + stringToQuote);
        }
        if (search.startsWith("^")) {
            search = "true:yes+" + search;
        }
        return search.replace("+", " AND ").replace("|", " OR ")
                .replace("^", " NOT ");
    }

    private String appendWithoutPunctuation(String str) {
        int strlen = str.length();
        StringBuilder sb = new StringBuilder(2 * strlen + 2);
        sb.append(str);
        sb.append(" ");
        for (int i = 0; i < strlen; i++) {
            char c = str.charAt(i);
            if (Character.isLetterOrDigit(c) || Character.isWhitespace(c)) {
                sb.append(c);
            } else {
                sb.append(" ");
            }
        }
        return sb.toString();
    }

}
