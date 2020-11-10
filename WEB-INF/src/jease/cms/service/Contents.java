package jease.cms.service;

import jease.cms.domain.*;
import jfix.db4o.*;

public class Contents {

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
