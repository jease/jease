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
package jease.site;

import org.eclipse.mylyn.wikitext.confluence.core.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.core.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.MarkupParser;
import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;
import org.eclipse.mylyn.wikitext.core.parser.markup.AbstractMarkupLanguage;
import org.eclipse.mylyn.wikitext.markdown.core.MarkdownLanguage;
import org.eclipse.mylyn.wikitext.mediawiki.core.MediaWikiLanguage;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.mylyn.wikitext.tracwiki.core.TracWikiLanguage;
import org.eclipse.mylyn.wikitext.twiki.core.TWikiLanguage;

import java.io.StringWriter;

/**
 * Common service methods to render different (Wiki)-Markups into HTML.
 */
public class Markups {

	public static final String CONFLUENCE = "Confluence";
	public static final String MARKDOWN = "Markdown";
	public static final String MEDIA_WIKI = "MediaWiki";
	public static final String TEXTILE = "Textile";
	public static final String TRAC_WIKI = "TracWiki";
	public static final String TWIKI = "TWiki";

	public static final String LINK_PATTERN = "{0}";

	public static String render(String content, String language, String pattern) {
		if (CONFLUENCE.equalsIgnoreCase(language)) {
			return renderConfluence(content, pattern);
		}
		if (MARKDOWN.equalsIgnoreCase(language)) {
			return renderMarkdown(content, pattern);
		}
		if (MEDIA_WIKI.equalsIgnoreCase(language)) {
			return renderMediaWiki(content, pattern);
		}
		if (TEXTILE.equalsIgnoreCase(language)) {
			return renderTextile(content, pattern);
		}
		if (TRAC_WIKI.equalsIgnoreCase(language)) {
			return renderTracWiki(content, pattern);
		}
		if (TWIKI.equalsIgnoreCase(language)) {
			return renderTWiki(content, pattern);
		}
		return content;
	}

	public static String renderConfluence(String content, String pattern) {
		return renderMarkup(content, new ConfluenceLanguage(), pattern);
	}

	public static String renderMarkdown(String content, String pattern) {
		return renderMarkup(content, new MarkdownLanguage(), pattern);
	}

	public static String renderMediaWiki(String content, String pattern) {
		return renderMarkup(content, new MediaWikiLanguage(), pattern);
	}

	public static String renderTextile(String content, String pattern) {
		return renderMarkup(content, new TextileLanguage(), pattern);
	}

	public static String renderTracWiki(String content, String pattern) {
		return renderMarkup(content, new TracWikiLanguage(), pattern);
	}

	public static String renderTWiki(String content, String pattern) {
		return renderMarkup(content, new TWikiLanguage(), pattern);
	}

	private static String renderMarkup(String content, AbstractMarkupLanguage markup, String pattern) {
		markup.setInternalLinkPattern(pattern);
		StringWriter writer = new StringWriter();
		DocumentBuilder builder = new HtmlDocumentBuilder(writer);
		MarkupParser parser = new MarkupParser(markup, builder);
		parser.parse(content, false);
		return writer.toString();
	}

}
