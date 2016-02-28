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

/**
 * A Folder contains an array of content-objects. A Folder defines #getContent()
 * which returns the first page-like child of the Folder as default content
 * which is used to render the Folder.
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
	 * moment the first page-like child of the folder (regardless if visible or
	 * not) is used.
	 */
	public Content getContent() {
		for (Content child : getChildren(Content.class)) {
			if (child.isPage()) {
				return child;
			}
		}
		return null;
	}

	public Folder copy(boolean recursive) {
		return (Folder) super.copy(recursive);
	}
}
