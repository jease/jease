package jease.cms.service;

import java.util.ArrayList;
import java.util.List;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Folder;
import jease.cms.domain.Reference;
import jease.cms.domain.Trash;
import jease.cms.domain.User;
import jfix.db4o.Database;
import jfix.functor.Functors;
import jfix.functor.Predicate;

public class Contents {

	/**
	 * Checks if content is referenced by Users or References. If no references
	 * exist, content can be deleted without dangling references in database.
	 */
	public static boolean isDeletable(Content content) {
		if (isDeleteGuardedByTrash(content)) {
			return true;
		}
		for (User user : Database.query(User.class)) {
			for (Folder folder : user.getRoots()) {
				if (folder.isDescendant(content)) {
					return false;
				}
			}
		}
		for (Reference reference : Database.query(Reference.class)) {
			if (reference.getContent() != null
					&& reference.getContent().isDescendant(content)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isDeleteGuardedByTrash(Content content) {
		return !(content instanceof Trash)
				&& content.getParents(Trash.class).length == 0
				&& content.getGuard(Trash.class) != null;
	}

	/**
	 * Deletes given content. If a Trash-Object is guarding the given content,
	 * the content will be moved to Trash, otherwise it will be deleted
	 * directly. If the given content is a Trash-Object, the Trash-Object is
	 * deleted only when it is empty, otherwise the Trash will be emptied.
	 */
	public static void delete(Content content) {
		if (content instanceof Trash) {
			Trash trash = (Trash) content;
			if (trash.isEmpty()) {
				Nodes.delete(trash);
			} else {
				trash.empty();
				Nodes.save(trash);
			}
		} else {
			Trash trash = content.getGuard(Trash.class);
			if (trash == null || content.isDescendant(trash)) {
				Nodes.delete(content);
			} else {
				trash.appendChild(content);
				Nodes.save(trash);
			}
		}
	}

	/**
	 * Returns only non privileged nodes from given array.
	 */
	public static Node[] filterNotPrivileged(Node[] nodes) {
		return Functors.filter(nodes, new Predicate<Node>() {
			public boolean test(Node node) {
				return !((Content) node).isPrivileged();
			}
		});
	}

	/**
	 * Returns all descendants for given nodes.
	 */
	public static Content[] getDescendants(Content[] nodes) {
		List<Content> contents = new ArrayList();
		for (Content node : nodes) {
			for (Content content : node.getDescendants(Content.class)) {
				contents.add(content);
			}
		}
		return contents.toArray(new Content[] {});
	}
}
