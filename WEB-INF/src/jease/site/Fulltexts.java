/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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

import java.util.ArrayList;
import java.util.List;

import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.functor.Supplier;
import jfix.search.FullTextIndex;
import jfix.util.Regexps;

/**
 * Service for searching through fulltext of content.
 */
public class Fulltexts {

	private static Supplier<FullTextIndex> fullTextIndex = new Supplier() {
		public FullTextIndex get() {
			FullTextIndex index = new FullTextIndex();
			for (Content content : Database.query(Content.class)) {
				if (content.isVisible()) {
					index.add(content, Regexps.stripTags(content.getFulltext()
							.toString()));
				}
			}
			index.commit();
			return index;
		}
	};

	/**
	 * Returns all visible content which matches the given query.
	 */
	public static List<Content> query(String query) {
		try {
			return (List<Content>) Database.query(fullTextIndex).search(query);
		} catch (Exception e) {
			return new ArrayList<Content>();
		}
	}
}
