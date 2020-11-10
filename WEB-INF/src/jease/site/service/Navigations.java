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
package jease.site.service;

import java.util.ArrayList;
import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.News;
import jease.cms.domain.Reference;

/**
 * Common service-methods to ease the building of navigations for a site.
 */
public class Navigations {

	/**
	 * Returns all visible folders contained in root node which should be
	 * displayed as tabs.
	 */
	public static Folder[] getTabs() {
		return ((Folder) Nodes.getRoot()).getVisibleChildren(Folder.class);
	}

	/**
	 * Returns all items (visible, not news) to be displayed in navigation for a
	 * given folder.
	 */
	public static Content[] getItems(Folder folder) {
		List<Content> navigation = new ArrayList();
		for (Content content : folder.getVisibleChildren(Content.class)) {
			if (!isNews(content)) {
				navigation.add(content);
			}
		}
		return navigation.toArray(new Content[] {});
	}

	/**
	 * Returns all news-objects for a given folder.
	 */
	public static News[] getNews(Folder folder) {
		List<News> news = new ArrayList();
		for (Content content : folder.getVisibleChildren(Content.class)) {
			if (isNews(content)) {
				news.add((News) (content instanceof News ? content
						: ((Reference) content).getContent()));
			}
		}
		return news.toArray(new News[] {});
	}

	/**
	 * Checks if given content is news or a reference to news.
	 */
	public static boolean isNews(Content content) {
		return content instanceof News
				|| (content instanceof Reference && ((Reference) content)
						.getContent() instanceof News);
	}

	/**
	 * Returns title (e.g. as page title) based on title of root node and title
	 * of given node.
	 */
	public static String getPageTitle(Node node) {
		return String.format("%s - %s", Nodes.getRoot().getTitle(), node
				.getTitle());
	}

	/**
	 * Returns path of root node with trailing slash.
	 */
	public static String getRootPath() {
		String rootPath = Nodes.getRoot().getPath();
		if (!rootPath.endsWith("/")) {
			rootPath = rootPath + "/";
		}
		return rootPath;
	}
}
