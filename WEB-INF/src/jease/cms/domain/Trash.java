package jease.cms.domain;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;

/**
 * Container which allows to store deleted objects until the trash is emptied.
 * A Trash-Container allows to hold several objects with identical ids.
 */
public class Trash extends Content {

	public Trash() {
	}

	public boolean isContainer() {
		return true;
	}

	public boolean isPage() {
		return false;
	}

	public Trash copy() {
		return (Trash) super.copy();
	}

	public void empty() {
		detachChildren();
	}

	public boolean isEmpty() {
		return getChildren().length == 0;
	}

	protected void validateDuplicate(Node potentialChild,
			String potentialChildId) throws NodeException {
		// Duplicates allowed
	}
}