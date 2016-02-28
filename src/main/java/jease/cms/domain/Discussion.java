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
package jease.cms.domain;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;

/**
 * A Discussion can hold information about author and comment and allows to
 * store only Discussions as children. This way a threaded discussion can be
 * created with a single content-type.
 */
public class Discussion extends Content {

	private String author;
	private String comment;

	public Discussion() {
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public synchronized void addComment(String title, String author,
			String text, boolean visible) {
		Discussion comment = new Discussion();
		comment.setId(getId() + "-" + (getChildren().length + 1));
		comment.setTitle(title);
		comment.setAuthor(author);
		comment.setComment(text);
		comment.setVisible(visible);
		comment.setLastModified(new java.util.Date());
		comment.setEditor(getEditor());
		comment.setParent(this);
	}

	public boolean isContainer() {
		return true;
	}

	protected void validateNesting(Node potentialChild, String potentialChildId)
			throws NodeException {
		super.validateNesting(potentialChild, potentialChildId);
		if (!(potentialChild instanceof Discussion)) {
			throw new NodeException.IllegalNesting();
		}
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(author).append("\n")
				.append(comment);
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setAuthor(getAuthor().replace(target, replacement));
		setComment(getComment().replace(target, replacement));
	}

	public Discussion copy(boolean recursive) {
		Discussion discussion = (Discussion) super.copy(recursive);
		discussion.setAuthor(getAuthor());
		discussion.setComment(getComment());
		return discussion;
	}
}
