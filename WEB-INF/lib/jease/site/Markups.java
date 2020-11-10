/*
    Copyright (C) 2011 maik.jablonski@jease.org

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
package jease.site;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.confluence.core.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

/**
 * Common service methods to render different (Wiki)-Markups into HTML.
 */
public class Markups {

	public static String renderConfluence(String content) {
		return renderMarkup(content, new ConfluenceLanguage());
	}

	public static String renderMediaWiki(String content) {
		return renderMarkup(content, new MediaWikiLanguage());
	}

	public static String renderTextile(String content) {
		return renderMarkup(content, new TextileLanguage());
	}

	public static String renderTracWiki(String content) {
		return renderMarkup(content, new TracWikiLanguage());
	}

	public static String renderTWiki(String content) {
		return renderMarkup(content, new TWikiLanguage());
	}

	private static String renderMarkup(String content, MarkupLanguage markup) {
		StringWriter writer = new StringWriter();
		DocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(markup, builder);
		parser.parse(content, false);
		return writer.toString();
	}

}
