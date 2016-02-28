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
package jease.cms.domain.property;

public class TextProperty extends Property {

	private String value;

	public TextProperty() {
	}

	public TextProperty(String name) {
		super(name);
	}

	public TextProperty(String name, String value) {
		this(name);
		setValue(value);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public TextProperty copy() {
		TextProperty property = (TextProperty) super.copy();
		property.setValue(getValue());
		return property;
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		if (getValue() != null) {
			setValue(getValue().replace(target, replacement));
		}
	}

	public String toString() {
		return value;
	}
}
