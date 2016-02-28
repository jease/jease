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
package jfix.util;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Common utilitites based on regular expressions.
 * 
 * Regular expressions for URLs copied from:
 * https://github.com/mzsanford/twitter-text-java License:
 * http://www.apache.org/licenses/LICENSE-2.0
 */
public class Regexps {

	private static final Pattern EXPRESSION_PATTERN = Pattern.compile(
			"\\$\\{(.*?)\\}", Pattern.DOTALL | Pattern.MULTILINE);

	private static final Pattern HTML_URLS = Pattern.compile(
			"(action|cite|href|src)=\"(.*?)\"", Pattern.DOTALL
					| Pattern.MULTILINE);

	/* URL related hash regex collection */
	private static final String URL_VALID_PRECEEDING_CHARS = "(?:[^\\-/\"':!=A-Z0-9_@＠]+|^|\\:)";
	private static final String URL_VALID_DOMAIN = "(?:[^\\p{Punct}\\s][\\.-](?=[^\\p{Punct}\\s])|[^\\p{Punct}\\s]){1,}\\.[a-z]{2,}(?::[0-9]+)?";

	private static final String URL_VALID_GENERAL_PATH_CHARS = "[a-z0-9!\\*';:=\\+\\$/%#\\[\\]\\-_,~]";
	private static final String URL_VALID_PATH_CHARS_WITHOUT_SLASH = "["
			+ URL_VALID_GENERAL_PATH_CHARS + "&&[^/]]";
	private static final String URL_VALID_PATH_CHARS_WITHOUT_COMMA = "["
			+ URL_VALID_GENERAL_PATH_CHARS + "&&[^,]]";

	/**
	 * Allow URL paths to contain balanced parens 1. Used in Wikipedia URLs like
	 * /Primer_(film) 2. Used in IIS sessions like /S(dfd346)/
	 **/
	private static final String URL_BALANCE_PARENS = "(?:\\("
			+ URL_VALID_GENERAL_PATH_CHARS + "+\\))";
	private static final String URL_VALID_URL_PATH_CHARS = "(?:"
			+ URL_BALANCE_PARENS + "|@" + URL_VALID_PATH_CHARS_WITHOUT_SLASH
			+ "++/" + "|(?:[.,]*+" + URL_VALID_PATH_CHARS_WITHOUT_COMMA + ")++"
			+ ")";

	/**
	 * Valid end-of-path chracters (so /foo. does not gobble the period). 2.
	 * Allow =&# for empty URL parameters and other URL-join artifacts
	 **/
	private static final String URL_VALID_URL_PATH_ENDING_CHARS = "(?:[a-z0-9=_#/\\-\\+]+|"
			+ URL_BALANCE_PARENS + ")";
	private static final String URL_VALID_URL_QUERY_CHARS = "[a-z0-9!\\*'\\(\\);:&=\\+\\$/%#\\[\\]\\-_\\.,~]";
	private static final String URL_VALID_URL_QUERY_ENDING_CHARS = "[a-z0-9_&=#/]";
	private static final String VALID_URL_PATTERN_STRING = "(" + // $1 total
																	// match
			"(" + URL_VALID_PRECEEDING_CHARS + ")" + // $2 Preceeding chracter
			"(" + // $3 URL
			"(https?://)" + // $4 Protocol
			"(" + URL_VALID_DOMAIN + ")" + // $5 Domain(s) and optional port
											// number
			"(/" + "(?:" + URL_VALID_URL_PATH_CHARS + "+|" + // 1+ path chars
																// and a valid
																// last char
			URL_VALID_URL_PATH_ENDING_CHARS + // Just a # case
			")?" + ")?" + // $6 URL Path and anchor
			"(\\?" + URL_VALID_URL_QUERY_CHARS + "*" + // $7 Query String
			URL_VALID_URL_QUERY_ENDING_CHARS + ")?" + ")" + ")";

	public static final Pattern VALID_URL = Pattern.compile(
			VALID_URL_PATTERN_STRING, Pattern.CASE_INSENSITIVE);
	public static final int VALID_URL_GROUP_ALL = 1;
	public static final int VALID_URL_GROUP_BEFORE = 2;
	public static final int VALID_URL_GROUP_URL = 3;
	public static final int VALID_URL_GROUP_PROTOCOL = 4;
	public static final int VALID_URL_GROUP_DOMAIN = 5;
	public static final int VALID_URL_GROUP_PATH = 6;
	public static final int VALID_URL_GROUP_QUERY_STRING = 7;

	/**
	 * Parses given template for expressions (${...}) and applies given
	 * transform-function on all expressions.
	 */
	public static String parseExpressions(String template,
			Function<String, String> transform) {
		Matcher matcher = EXPRESSION_PATTERN.matcher(template);
		while (matcher.find()) {
			template = template.replace(matcher.group(),
					transform.apply(matcher.group(1)));
		}
		return template;
	}

	/**
	 * Converts given plain text into HTML by replacing newlines with paragraphs
	 * and urls with links.
	 */
	public static String convertTextToHtml(String text) {
		StringBuilder sb = new StringBuilder();
		text = text.replaceAll("\r", "").replaceAll("\n\n\n*", "\n\n");
		for (int index = 0; index < text.length(); index += 2) {
			int start = index;
			index = text.indexOf("\n\n", start);
			if (index < 0) {
				index = text.length();
			}
			String para = text.substring(start, index);
			if (para.length() > 0) {
				sb.append("<p>");
				sb.append(convertUrlsToLinks(para.replace("\n", "<br />\n")));
				sb.append("</p>\n");
			}
		}
		return sb.toString();
	}

	/**
	 * Converts all urls in given text into links.
	 */
	public static String convertUrlsToLinks(String text) {
		Matcher matcher = VALID_URL.matcher(text);
		StringBuffer sb = new StringBuffer(text.length());
		while (matcher.find()) {
			String protocol = matcher.group(VALID_URL_GROUP_PROTOCOL);
			if (!protocol.isEmpty()) {
				String url = matcher.group(VALID_URL_GROUP_URL);
				matcher.appendReplacement(sb, String.format(
						"$%s<a href=\"%s\">%s</a>", VALID_URL_GROUP_BEFORE,
						url, url));
				continue;
			}
			matcher.appendReplacement(sb,
					String.format("$%s", VALID_URL_GROUP_ALL));
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	/**
	 * Returns list of http(s)-urls contained in given text.
	 */
	public static List<String> extractUrls(String text) {
		if (text == null) {
			return null;
		}
		List<String> urls = new ArrayList<String>();
		Matcher matcher = VALID_URL.matcher(text.replace("\"", " "));
		while (matcher.find()) {
			if (!matcher.group(VALID_URL_GROUP_PROTOCOL).isEmpty()) {
				urls.add(matcher.group(VALID_URL_GROUP_URL));
			}
		}
		return urls;
	}

	/**
	 * Returns true if given url is a full qualified valid http(s)-url.
	 */
	public static boolean isValidUrl(String url) {
		if (url == null) {
			return false;
		}
		return VALID_URL.matcher(url).matches();
	}

	/**
	 * Returns all urls contained in attributes in given html.
	 */
	public static List<String> extractUrlsFromHtml(String html) {
		List<String> result = new ArrayList<String>();
		Matcher matcher = HTML_URLS.matcher(html);
		while (matcher.find()) {
			result.add(matcher.group(2));
		}
		return result;
	}
}
