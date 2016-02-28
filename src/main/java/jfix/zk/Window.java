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
package jfix.zk;

public class Window extends org.zkoss.zul.Window {

	public Window() {
		this("?", true);
	}

	public Window(String title) {
		this(title, true);
	}

	public Window(String title, boolean closable) {
		this(title, "normal", closable);
	}

	public Window(String title, String border, boolean closable) {
		super(title, border, closable);
		doOverlapped();
		setPosition("none");
		setWidth("70%");
		setLeft("15%");
		setTop("2%");
	}

	/**
	 * Closes the window by detaching it from parent.
	 */
	public void close() {
		setParent(null);
	}

	public void setTitle(String title) {
		title = (title == null) ? "?" : title.trim().replace("null", "?");
		super.setTitle("".equals(title) ? "..." : title);
	}

	public void setTitle(Object obj) {
		if (obj == null) {
			setTitle("...");
		} else {
			String title = obj.toString();
			if (title == null) {
				setTitle("...");
			} else {
				setTitle(title);
			}
		}
	}

	public void doModal() {
		doHighlighted();
	}
}
