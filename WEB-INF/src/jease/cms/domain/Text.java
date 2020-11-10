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

public class Text extends Content {

	private String content;
	private boolean plain;
	
	public Text() {
	}
	
	public void setContent(String content) {
		this.content = content;
	}

	public String getContent() {
		return content;
	}

	public boolean isPlain() {
		return plain;
	}

	public void setPlain(boolean plain) {
		this.plain = plain;
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getContent());
	}
	
	public long getSize() {
		return super.getSize() + getContent().length();
	}
		
	public Text copy() {
		Text text = (Text) super.copy();
		text.setContent(getContent());
		text.setPlain(isPlain());
		return text;
	}
}
