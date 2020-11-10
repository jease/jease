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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.Reference;
import jease.cms.domain.Trash;
import jfix.db4o.Database;
import jfix.functor.Predicate;
import jfix.functor.Supplier;
import jfix.search.FullTextIndex;
import jfix.util.Regexps;
import jfix.util.Validations;

/**
 * Service for searching through fulltext of content.
 */
public class Fulltexts {

	private static Supplier<FullTextIndex> fullTextIndex = new Supplier() {
		public FullTextIndex get() {
			FullTextIndex index = new FullTextIndex();
			for (Content content : getContents()) {
				index.add(content,
						Regexps.stripTags(content.getFulltext().toString()));
			}
			index.commit();
			return index;
		}
	};

	private static Collection<Content> getContents() {
		return Database.query(Content.class, new Predicate<Content>() {
			public boolean test(Content content) {
				return isDefault(content) || isPublic(content);
			}
		});
	}

	/**
	 * Checks if given content is default content of Folder or Reference.
	 */
	private static boolean isDefault(Content content) {
		if (content.getParent() != null) {
			Content parent = (Content) content.getParent();
			if (isPublic(parent)
					&& (((parent instanceof Folder) && ((Folder) parent)
							.getContent() == content) || (((parent instanceof Reference) && ((Reference) parent)
							.getContent() == content)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if given content is available for public access.
	 */
	private static boolean isPublic(Content content) {
		return content.isVisible()
				&& Validations.isEmpty(content.getParents(Trash.class))
				&& !Authorizations.isGuarded(content);
	}

	/**
	 * Returns all visible content which descends from given context and matches
	 * the given query.
	 */
	public static List<Content> query(String query) {
		try {
			List<Content> result = new ArrayList();
			for (Content content : (List<Content>) Database
					.query(fullTextIndex).search(query)) {
				// When content is child of a "paged container" (e.g.
				// Composite),
				// traverse upwards to the top-level container.
				Content target = content;
				while (target.getParent() != null
						&& ((Content) target.getParent()).isPage()
						&& ((Content) target.getParent()).isContainer()) {
					target = (Content) target.getParent();
				}
				if (!result.contains(target)) {
					result.add(target);
				}
			}
			return result;
		} catch (Exception e) {
			return new ArrayList<Content>();
		}
	}
}
