package jease.site.service;

import java.util.*;

import jease.cmf.service.*;
import jease.cms.domain.*;

public class Navigations {

	public static Folder[] getTabs() {
		return ((Folder) Nodes.getRoot()).getVisibleChildren(Folder.class);
	}

	public static Content[] getItems(Folder folder) {
		List<Content> navigation = new ArrayList();
		for (Content content : folder.getVisibleChildren(Content.class)) {
			if (!isNews(content)) {
				navigation.add(content);
			}
		}
		return navigation.toArray(new Content[] {});
	}

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

	public static boolean isNews(Content content) {
		return content instanceof News
				|| (content instanceof Reference && ((Reference) content)
						.getContent() instanceof News);
	}

}
