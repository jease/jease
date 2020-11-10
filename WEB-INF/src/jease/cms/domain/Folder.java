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

/**
 * A Folder contains an array of content-objects. A Folder defines 
 * #getContent() which returns the first child of the Folder as default
 * content which is used to render the Folder.
 */
public class Folder extends Content {

	public Folder() {
	}

	public boolean isContainer() {
		return true;
	}

	public boolean isPage() {
		return false;
	}

	/**
	 * Returns the content which should be used to render the folder. At the
	 * moment the first child of the folder (regardless if visible or not) is
	 * used.
	 */
	public Content getContent() {
		for (Content child : getChildren(Content.class)) {
			return child;
		}
		return null;
	}

	public StringBuilder getFulltext() {
		Content content = getContent();
		if (content != null) {
			return super.getFulltext().append(content.getFulltext());
		} else {
			return super.getFulltext();
		}
	}

	public Folder copy() {
		return (Folder) super.copy();
	}
}
