/*
    Copyright (C) 2014 maik.jablonski@jease.org

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

import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.LimitTokenCountAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.FieldInfo.IndexOptions;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

public class FullTextIndex<E> {

	// + - && || ! ( ) { } [ ] ^ " ~ * ? : \
	private static final String[] STRINGS_TO_QUOTE = new String[] { "-", "&",
			"!", "{", "}", "[", "]", ":", "?" };

	static {
		BooleanQuery.setMaxClauseCount(BooleanQuery.getMaxClauseCount() * 10);
	}

	private List<E> objects;
	private Analyzer analyzer;
	private Directory indexDirectory;
	private IndexWriter indexWriter;
	private QueryParser queryParser;
	private Document document;
	private Field fulltext;
	private Field inverse;

	public FullTextIndex() {
		try {
			objects = new ArrayList<E>();

			analyzer = new SimpleAnalyzer();
			indexDirectory = new RAMDirectory();
			indexWriter = new IndexWriter(indexDirectory,
					new IndexWriterConfig(Version.LUCENE_36,
							new LimitTokenCountAnalyzer(analyzer,
									Integer.MAX_VALUE))
							.setOpenMode(OpenMode.CREATE));
			queryParser = new QueryParser(Version.LUCENE_36, "text", analyzer);
			queryParser.setDefaultOperator(QueryParser.AND_OPERATOR);

			fulltext = new Field("text", "", Field.Store.NO,
					Field.Index.ANALYZED);
			fulltext.setIndexOptions(IndexOptions.DOCS_ONLY);

			// Used as base-set for a NOT-Query
			inverse = new Field("true", "yes", Field.Store.NO,
					Field.Index.ANALYZED);
			inverse.setIndexOptions(IndexOptions.DOCS_ONLY);

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
				fulltext.setValue(appendWithoutPunctuation(text));
				indexWriter.addDocument(document);
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public void commit() {
		try {
			indexWriter.close();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public List<E> search(String search) throws Exception {
		try {
			ObjectCollector<E> collector = new ObjectCollector<E>(objects);
			String query = buildQueryString(search);
			IndexSearcher indexSearcher = new IndexSearcher(
					IndexReader.open(indexDirectory));
			indexSearcher.search(queryParser.parse(query), collector);
			indexSearcher.close();
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
