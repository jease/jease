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

import java.util.Date;

/**
 * News-Object which consists of a teaser, a story and a publication date.
 */
public class News extends Content {

	private String teaser;
	private String story;
	private Date date;

	public News() {
	}

	public String getTeaser() {
		return teaser;
	}

	public void setTeaser(String teaser) {
		this.teaser = teaser;
	}

	public String getStory() {
		return story;
	}

	public void setStory(String story) {
		this.story = story;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public StringBuilder getFulltext() {
		return super
				.getFulltext()
				.append("\n")
				.append(getTeaser())
				.append("\n")
				.append(getStory())
				.append("\n")
				.append(getDate() != null ? String.format("%tF", getDate())
						: "");
	}

	public long getSize() {
		return super.getSize() + getTeaser().length() + getStory().length();
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setTeaser(getTeaser().replace(target, replacement));
		setStory(getStory().replace(target, replacement));
	}

	public News copy(boolean recursive) {
		News news = (News) super.copy(recursive);
		news.setTeaser(getTeaser());
		news.setStory(getStory());
		news.setDate(getDate());
		return news;
	}
}
