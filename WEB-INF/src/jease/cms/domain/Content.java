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
package jease.cms.domain;

import java.util.Date;

import jease.cmf.domain.Node;

/**
 * Abstract base class for building up a Content-Management-System.
 * 
 * This class stores a title, the date and the user of the last modification and a
 * flag which denotes if the content should be displayed in automatic generated
 * lists when displayed in the public site.
 */
public abstract class Content extends Node {

	private String title;
	private Date lastModified;
	private User editor;
	private boolean visible;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * If true, the content should be visible in automatically created lists
	 * (e.g. navigations, search results).
	 */
	public boolean isVisible() {
		return visible;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	/**
	 * Returns the user who was the last one who edited the content.
	 */
	public User getEditor() {
		return editor;
	}

	/**
	 * If true, the content-type is a container and can hold other nodes as
	 * children.
	 */
	public boolean isContainer() {
		return false;
	}

	/**
	 * Returns the size of the content in bytes. Derived implementations should
	 * add additional size information to super.getSize().
	 */
	public long getSize() {
		return super.getSize() + getTitle().length();
	}

	/**
	 * Returns a fulltext-representation of the content as StringBuilder, so
	 * derived contents can append additional information directly to
	 * super.getFulltext() without the need to create new strings.
	 */
	public StringBuilder getFulltext() {
		return new StringBuilder().append(getId()).append("\n").append(
				getTitle()).append("\n").append(getType());
	}

	/**
	 * If true, the content should rendered as page (e.g. with surrounding
	 * layout), otherwise the content is delivered directly.
	 */
	public boolean isPage() {
		return true;
	}

	/**
	 * If true, the content can perform serious operations with security risks
	 * involved (e.g. access file-system, scripting), so only privileged users
	 * (e.g. administrators) should be able to create them.
	 */
	public boolean isPrivileged() {
		return false;
	}

	/**
	 * Returns guard object of given type in upstream direction by traversing
	 * all children of all parents for given content. In general a guard is a
	 * child in one of the parent folders of the given object which should have
	 * some influence on behaviour of the given object.
	 */
	public <E extends Content> E getGuard(Class<E> type) {
		Node currentParent = getParent();
		while (currentParent != null) {
			for (E node : currentParent.getChildren(type)) {
				return node;
			}
			currentParent = currentParent.getParent();
		}
		return null;
	}
	
	/**
	 * Creates a controlled copy of the content. In derived implementations,
	 * super.copy() should be called and then additional fields should be
	 * copied. Elements of a container will copied automatically.
	 */
	public Content copy() {
		Content content = (Content) super.copy();
		content.setTitle(getTitle());
		content.setLastModified(getLastModified());
		content.setEditor(getEditor());
		content.setVisible(isVisible());
		return content;
	}
}
