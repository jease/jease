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
package jease.cms.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jease.cms.domain.Content;
import jease.cms.domain.Linkcheck;
import jfix.db4o.Database;
import jfix.util.Regexps;
import jfix.util.Urls;

public class Linkchecker implements Runnable {

	private static final Pattern INTERNAL_URL_PATH = Pattern.compile(
			"^(.*?)(;|#|\\?)(.*)$", Pattern.DOTALL | Pattern.MULTILINE);
	private static int TIMEOUT = 60;
	private static boolean active = false;

	public static synchronized boolean isActive() {
		return active;
	}

	public static synchronized void start() {
		new Thread(new Linkchecker()).start();
	}

	public synchronized void run() {
		if (active == false) {
			try {
				active = true;
				clear();
				check();
			} finally {
				active = false;
			}
		}
	}

	/**
	 * Deletes all linkcheck objects from database.
	 */
	public static void clear() {
		Database.write(new Runnable() {
			public void run() {
				for (Linkcheck linkcheck : Database.query(Linkcheck.class)) {
					Database.delete(linkcheck);
				}
			}
		});
	}

	/**
	 * Deletes all linkcheck objects from database with given path.
	 */
	public static void clear(String path) {
		for (Linkcheck linkcheck : Database.query(Linkcheck.class)) {
			if (path.equals(linkcheck.getPath())) {
				Database.delete(linkcheck);
			}
		}
	}

	/**
	 * Performs full link check and saves status to database.
	 */
	public static void check() {
		Map<String, Integer> linkStates = new HashMap<>();
		for (Content content : Database.query(Content.class)) {
			String fulltext = content.getFulltext().toString();
			for (String url : extractUrls(fulltext)) {
				int status;
				if (linkStates.containsKey(url)) {
					status = linkStates.get(url);
				} else {
					status = getStatus(content, url);
					linkStates.put(url, status);
				}
				Database.save(new Linkcheck(content.getPath(), url, status));
			}
		}
	}

	/**
	 * For every URL in given content object perform a linkcheck and save status
	 * to the database.
	 */
	public static void check(Content content) {
		String fulltext = content.getFulltext().toString();
		for (String url : extractUrls(fulltext)) {
			Database.save(new Linkcheck(content.getPath(), url, getStatus(
					content, url)));
		}
	}

	private static Set<String> extractUrls(String fulltext) {
		return new HashSet<>(Regexps.extractUrlsFromHtml(fulltext));
	}

	private static int getStatus(Content content, String url) {
		if (url.startsWith("http:") || url.startsWith("https:")) {
			return Urls.getStatus(url, TIMEOUT);
		} else {
			// Unknown protocol (e.g mailto: or file:) or scripting element
			if (url.contains(":") || url.startsWith("<") || url.startsWith("$")) {
				return -1;
			}
			Matcher matcher = INTERNAL_URL_PATH.matcher(url);
			if (matcher.matches()) {
				url = matcher.group(1);
			}
			if (url.startsWith("./~")) {
				url = url.substring(3);
			}
			return content.getChild(url) != null ? 200 : 404;
		}
	}
}
