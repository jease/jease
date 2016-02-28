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

import org.zkoss.zk.ui.Component;

/**
 * Wraps a generic value and a component. A View is to be used in a TableModel,
 * when you want to display a complex component (or different label), although
 * the table should use the given value for indexing, sorting and so on.
 */
public class View<E> {

	private E value;
	private Component component;

	public View() {
	}

	public View(E value, Component component) {
		this.value = value;
		this.component = component;
	}

	public E getValue() {
		return value;
	}

	public Component getComponent() {
		return component;
	}

	public void setValue(E value) {
		this.value = value;
	}

	public void setComponent(Component component) {
		this.component = component;
	}
}
