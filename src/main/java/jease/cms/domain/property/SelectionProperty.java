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

public class SelectionProperty extends Property {

	private String value;
	private String provider;

	public SelectionProperty() {
	}

	public SelectionProperty(String name) {
		super(name);
	}

	public SelectionProperty(String name, String value) {
		this(name);
		setValue(value);
	}

	public SelectionProperty(String name, String value, String provider) {
		this(name);
		setValue(value);
		setProvider(provider);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String path) {
		this.provider = path;
	}

	public void cloneTo(Property clone) {
		super.cloneTo(clone);
		((SelectionProperty) clone).setProvider(getProvider());
	}

	public SelectionProperty copy() {
		SelectionProperty property = (SelectionProperty) super.copy();
		property.setValue(getValue());
		property.setProvider(getProvider());
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
