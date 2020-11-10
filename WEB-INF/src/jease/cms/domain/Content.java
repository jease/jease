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

	public boolean isVisible() {
		return visible;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	public User getEditor() {
		return editor;
	}

	public boolean isContainer() {
		return false;
	}

	public long getSize() {
		return super.getSize() + getTitle().length();
	}

	public StringBuilder getFulltext() {
		return new StringBuilder().append(getId()).append("\n").append(
				getTitle()).append("\n").append(getType());
	}

	public boolean isPage() {
		return true;
	}

	public Content copy() {
		Content content = (Content) super.copy();
		content.setTitle(getTitle());
		content.setLastModified(getLastModified());
		content.setEditor(getEditor());
		content.setVisible(isVisible());
		return content;
	}
}
