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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.News;
import jease.cms.domain.Reference;
import jfix.functor.Functors;
import jfix.functor.Predicate;

/**
 * Common service-methods to ease the building of navigations for a site.
 */
public class Navigations {

	/**
	 * Returns all visible folders contained in root node which should be
	 * displayed as tabs.
	 */
	public static Content[] getTabs() {
		return Functors.filter(Nodes.getRoot().getChildren(Content.class),
				new Predicate<Content>() {
					public boolean test(Content content) {
						return content instanceof Folder && content.isVisible();
					}
				});
	}

	/**
	 * Returns all items (visible, not news) to be displayed in navigation for a
	 * given container.
	 */
	public static Content[] getItems(Content container) {
		List<Content> navigation = new ArrayList();
		for (Content content : container.getChildren(Content.class)) {
			if (content.isVisible() && !isNews(content)) {
				navigation.add(content);
			}
		}
		return navigation.toArray(new Content[] {});
	}

	/**
	 * Returns all news-objects for a given container. If no news exist within
	 * given container, news from parent container will be returned and so on.
	 */
	public static News[] getNews(Content container) {
		List<News> news = new ArrayList();
		for (Content content : container.getChildren(Content.class)) {
			if (content.isVisible() && isNews(content)) {
				news.add((News) (content instanceof News ? content
						: ((Reference) content).getDestination()));
			}
		}
		if (news.isEmpty() && container.getParent() != null) {
			return getNews((Content) container.getParent());
		}
		return news.toArray(new News[] {});
	}

	/**
	 * Checks if given content is news or a reference to news.
	 */
	public static boolean isNews(Content content) {
		if (content.getParent() instanceof Folder
				&& ((Folder) content.getParent()).getContent() == content) {
			return false;
		}
		return content instanceof News
				|| (content instanceof Reference && ((Reference) content)
						.getDestination() instanceof News);
	}

	/**
	 * Returns all visible items contained in given container.
	 */
	public static Content[] getVisibleContent(Content container) {
		List<Content> navigation = new ArrayList();
		for (Content content : container.getChildren(Content.class)) {
			if (content.isVisible()) {
				navigation.add(content);
			}
		}
		return navigation.toArray(new Content[] {});
	}

	/**
	 * Returns title (e.g. as page title) based on title of root node and title
	 * of given node.
	 */
	public static String getPageTitle(Content content) {
		return String.format("%s - %s", ((Content) Nodes.getRoot()).getTitle(),
				content.getTitle());
	}

	/**
	 * Returns path of root node with trailing slash.
	 */
	public static String getRootPath() {
		String rootPath = Nodes.getRoot().getPath();
		if (rootPath.endsWith("/")) {
			return rootPath;
		} else {
			return rootPath + "/";
		}
	}

	/**
	 * Returns path of given node (with trailing slash for containers).
	 */
	public static String getBasePath(Node node) {
		return node.isContainer() ? node.getPath() + "/" : node.getPath();
	}

	/**
	 * Returns all News sorted by publication date which are visible by itself
	 * or via a reference (Reference or Folder).
	 */
	public static News[] getSiteNews() {
		Set<News> news = new HashSet();
		for (Content content : Nodes.getRoot().getDescendants(Content.class)) {
			if (content.isVisible()) {
				News candidate = null;
				if (content instanceof News) {
					candidate = (News) content;
				}
				if (content instanceof Reference
						&& ((Reference) content).getContent() instanceof News) {
					candidate = (News) ((Reference) content).getContent();
				}
				if (content instanceof Folder
						&& ((Folder) content).getContent() instanceof News) {
					candidate = (News) ((Folder) content).getContent();
				}
				if (candidate != null && candidate.getDate() != null) {
					news.add(candidate);
				}
			}
		}
		List<News> result = new ArrayList(news);
		Collections.sort(result, new java.util.Comparator<News>() {
			public int compare(News o1, News o2) {
				return o2.getDate().compareTo(o1.getDate());
			}
		});
		return result.toArray(new News[] {});
	}
}
