/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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

import jfix.functor.Functors;
import jfix.functor.Predicate;

public class Folder extends Content {

	public Folder() {
	}

	public boolean isContainer() {
		return true;
	}

	public boolean isPage() {
		return false;
	}

	public <E extends Content> E[] getVisibleChildren(Class<E> clazz) {
		return Functors.filter(getChildren(clazz), new Predicate<E>() {
			public boolean test(E content) {
				return content.isVisible();
			}
		});
	}

	public Content getDefaultContent() {
		for (Content child : getChildren(Content.class)) {
			if (child.isVisible()) {
				return child;
			}
		}
		return null;
	}

	public Folder copy() {
		return (Folder) super.copy();
	}
}
