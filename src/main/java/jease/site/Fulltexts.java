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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.apache.commons.lang3.ArrayUtils;
import org.zkoss.zsoup.Zsoup;

import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.Reference;
import jease.cms.domain.Trash;
import jfix.db4o.Database;
import jfix.search.FullTextIndex;

/**
 * Service for searching through fulltext of content.
 */
public class Fulltexts {

	private static Supplier<FullTextIndex<Content>> fullTextIndex = () -> {
		FullTextIndex<Content> index = new FullTextIndex<>();
		for (Content content : getContents()) {
			index.add(content, Zsoup.parseBodyFragment(content.getFulltext().toString()).text());
		}
		index.commit();
		return index;
	};

	private static Collection<Content> getContents() {
		return Database.query(Content.class, $content -> isDefault($content) || isPublic($content));
	}

	/**
	 * Checks if given content is default content of Folder or Reference.
	 */
	private static boolean isDefault(Content content) {
		if (content.getParent() != null) {
			Content parent = (Content) content.getParent();
			if (isPublic(parent) && (((parent instanceof Folder) && ((Folder) parent).getContent() == content)
					|| (((parent instanceof Reference) && ((Reference) parent).getContent() == content)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if given content is available for public access.
	 */
	private static boolean isPublic(Content content) {
		return content.isVisible() && ArrayUtils.isEmpty(content.getParents(Trash.class));
	}

	/**
	 * Returns all visible content which descends from given context and matches
	 * the given query.
	 */
	public static List<Content> query(Content context, String query) {
		try {
			List<Content> result = new ArrayList<>();
			for (Content content : Database.query(fullTextIndex).search(query)) {
				// When content is child of a "paged container" (e.g.
				// Composite), traverse upwards to the top-level container.
				Content target = content;
				while (target.getParent() != null && ((Content) target.getParent()).isPage()
						&& target.getParent().isContainer()) {
					target = (Content) target.getParent();
				}
				if (!result.contains(target) && (context == null || target.isDescendant(context))) {
					result.add(target);
				}
			}
			return result;
		} catch (Exception e) {
			return Collections.EMPTY_LIST;
		}
	}

	public static List<Content> query(String query) {
		return query(null, query);
	}
}
