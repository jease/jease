package jease.cms.service;

import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.Reference;
import jease.cms.domain.User;
import jfix.db4o.Database;

public class Contents {

	/**
	 * Checks if content is referenced by Users or References. If no references
	 * exist, content can be deleted without dangling references in database.
	 */
	public static boolean isDeletable(Content content) {
		for (User user : Database.query(User.class)) {
			for (Folder folder : user.getRoots()) {
				if (folder.isDescendant(content)) {
					return false;
				}
			}
		}
		for (Reference reference : Database.query(Reference.class)) {
			if (reference.getContent().isDescendant(content)) {
				return false;
			}
		}
		return true;
	}
}
