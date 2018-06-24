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
 * Class for all kinds of markup oriented content which can be rendered into
 * XHTML.
 */
public class Wiki extends Content {

	private String content;

	public Wiki() {
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getContent());
	}

	public long getSize() {
		return super.getSize() + getContent().length();
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setContent(getContent().replace(target, replacement));
	}

	public Wiki copy(boolean recursive) {
		Wiki wiki = (Wiki) super.copy(recursive);
		wiki.setContent(getContent());
		return wiki;
	}
}
