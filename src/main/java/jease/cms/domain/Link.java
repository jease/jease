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
 * A Link-Object stores an url to another web-site.
 */
public class Link extends Content {

	private String url;

	public Link() {
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public boolean isPage() {
		return false;
	}

	public long getSize() {
		return super.getSize() + getUrl().length();
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getUrl());
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setUrl(getUrl().replace(target, replacement));
	}

	public Link copy(boolean recursive) {
		Link link = (Link) super.copy(recursive);
		link.setUrl(getUrl());
		return link;
	}
}