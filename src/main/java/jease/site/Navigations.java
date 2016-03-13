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

import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.News;
import jease.cms.domain.Reference;

import java.util.*;
import java.util.stream.Stream;

/**
 * Common service-methods to ease the building of navigations for a site.
 */
public class Navigations {

	/**
	 * Returns global root node.
	 */
	public static Content getRoot() {
		return (Content) Nodes.getRoot();
	}

	/**
	 * Returns true if given content is root node or default content of root
	 * node.
	 */
	public static boolean isRoot(Content content) {
		return getRoot() == content
				|| (getRoot() instanceof Folder && ((Folder) getRoot()).getContent() == content);
	}

	/**
	 * Returns path of root node with trailing slash.
	 */
	public static String getRootPath() {
		return getBasePath(getRoot());
	}

	/**
	 * Returns path of given node (with trailing slash for containers).
	 */
	public static String getBasePath(Content node) {
		String path = node.getPath();
		if (node.isContainer() && !path.endsWith("/")) {
			return path + "/";
		} else {
			return path;
		}
	}

	/**
	 * Returns all visible folders contained in given container which should be
	 * displayed as tabs.
	 */
	public static Content[] getTabs(Content container) {
		return Stream.of(container.getChildren(Content.class))
				.filter(content -> content instanceof Folder && content.isVisible())
				.toArray(Content[]::new);
	}

	/**
	 * Returns all visible folders contained in root node which should be
	 * displayed as tabs.
	 */
	public static Content[] getTabs() {
		return getTabs(getRoot());
	}

	/**
	 * Returns all parents for given content which are descending from given
	 * root.
	 */
	public static Content[] getBreadcrumb(final Content root, final Content content) {
		return Stream.of(getBreadcrumb(content))
				.filter(breadcrumb -> breadcrumb.isDescendant(root))
				.toArray(Content[]::new);
	}

	/**
	 * Returns all parents for given content descending from absolute root.
	 */
	public static Content[] getBreadcrumb(final Content content) {
		return content.getParents(Content.class);
	}

	/**
	 * Returns all items (visible, not news) to be displayed in navigation for a
	 * given container.
	 */
	public static Content[] getItems(final Content container) {
		return Stream.of(container.getChildren(Content.class))
				.filter(content -> content.isVisible() && !isNews(content))
				.toArray(Content[]::new);
	}

	/**
	 * Returns all news-objects for a given container. If no news exist within
	 * given container, news from parent container will be returned and so on.
	 */
	public static News[] getNews(Content container) {
		List<News> news = new ArrayList<>();
		for (Content content : container.getChildren(Content.class)) {
			if (content.isVisible() && isNews(content)) {
				news.add((News) (content instanceof News ? content : ((Reference) content).getDestination()));
			}
		}
		if (news.isEmpty() && container.getParent() != null) {
			return getNews((Content) container.getParent());
		}
		return news.toArray(new News[news.size()]);
	}

	/**
	 * Checks if given content is news or a reference to news.
	 */
	public static boolean isNews(Content content) {
		if (content.getParent() instanceof Folder && ((Folder) content.getParent()).getContent() == content) {
			return false;
		}
		return content instanceof News
				|| (content instanceof Reference && ((Reference) content).getDestination() instanceof News);
	}

	/**
	 * Returns all visible items contained in given container.
	 */
	public static Content[] getVisibleContent(Content container) {
		return Stream.of(container.getChildren(Content.class)).filter(Content::isVisible).toArray(Content[]::new);
	}

	/**
	 * Returns title (e.g. as page title) based on title of root node, the
	 * default content of the root node and the title of given node.
	 */
	public static String getPageTitle(Content content) {
		Folder root = (Folder) getRoot();
		if (root.getContent() != null && root.getContent() != content) {
			return String.format("%s | %s | %s", content.getTitle(), root.getContent().getTitle(), root.getTitle());
		} else {
			return String.format("%s | %s", content.getTitle(), root.getTitle());
		}
	}

	/**
	 * Returns all News sorted by publication date which are visible by itself
	 * or via a reference (Reference or Folder) for all children descending from
	 * given container.
	 */
	public static News[] getSiteNews(Content container) {
		Set<News> news = new HashSet<>();
		for (Content content : container.getDescendants(Content.class)) {
			if (content.isVisible()) {
				News candidate = null;
				if (content instanceof News) {
					candidate = (News) content;
				}
				if (content instanceof Reference && ((Reference) content).getContent() instanceof News) {
					candidate = (News) ((Reference) content).getContent();
				}
				if (content instanceof Folder && ((Folder) content).getContent() instanceof News) {
					candidate = (News) ((Folder) content).getContent();
				}
				if (candidate != null && candidate.getDate() != null) {
					news.add(candidate);
				}
			}
		}
		List<News> result = new ArrayList<>(news);
		result.sort(Comparator.comparing(News::getDate).reversed());
		return result.toArray(new News[result.size()]);
	}

	/**
	 * Returns all News sorted by publication date which are visible by itself
	 * or via a reference (Reference or Folder) for the entire site.
	 */
	public static News[] getSiteNews() {
		return getSiteNews(getRoot());
	}

	/**
	 * Returns the last modified child if given content is a container.
	 * Otherwise the given content is returned.
	 */
	public static Content getLatestContribution(Content content) {
		Content latestContribution = content;
		if (content.isContainer()) {
			for (Content child : content.getChildren(Content.class)) {
				if (child.getLastModified().after(latestContribution.getLastModified())) {
					latestContribution = child;
				}
			}
		}
		return latestContribution;
	}
}
