package jease.cms.service;

import java.util.*;

import jease.cmf.domain.*;
import jease.cmf.service.*;
import jease.cms.domain.*;
import jfix.db4o.*;
import jfix.functor.*;

public class Contents {

	public static boolean isDeletable(Content content) {
		for (User user : Database.query(User.class)) {
			for (Folder folder : user.getRoots()) {
				if (hasReference(folder, content)) {
					return false;
				}
			}
		}
		for (Reference reference : Database.query(Reference.class)) {
			if (hasReference(reference.getContent(), content)) {
				return false;
			}
		}
		return true;
	}

	private static boolean hasReference(Node node, Node possibleParent) {
		return node == possibleParent
				|| jfix.util.Arrays.contains(node.getParents(), possibleParent);
	}

	public static List<Content> getContents() {
		final List<Content> pages = new ArrayList();
		Nodes.getRoot().traverse(new Procedure<Node>() {
			public void execute(Node node) {
				if (node instanceof Content) {
					pages.add((Content) node);
				}
			}
		});
		return pages;
	}
}
