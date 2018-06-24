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
 * A reference is a symbolic link to another content-object. A reference can be
 * used to mirror the same content at different places (think of identical news
 * on frontpage and some subfolders).
 */
public class Reference extends Content {

	private Content content;

	public Reference() {
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

	/**
	 * Resolves chain of linked References and returns first content which is
	 * not a Reference.
	 */
	public Content getDestination() {
		Content destination = content;
		while (destination instanceof Reference) {
			destination = ((Reference) destination).getContent();
		}
		return destination;
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n")
				.append(content != null ? content.getPath() : "");
	}

	public boolean isPage() {
		if (content != null) {
			return content.isPage();
		}
		return true;
	}

	public Reference copy(boolean recursive) {
		Reference reference = (Reference) super.copy(recursive);
		reference.setContent(getContent());
		return reference;
	}
}