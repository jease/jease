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

import java.util.Date;

public class DateProperty extends Property {

	private Date value;

	public DateProperty() {
	}

	public DateProperty(String name) {
		super(name);
	}

	public DateProperty(String name, Date value) {
		this(name);
		setValue(value);
	}

	public Date getValue() {
		return value;
	}

	public void setValue(Date value) {
		this.value = value;
	}

	public DateProperty copy() {
		DateProperty property = (DateProperty) super.copy();
		property.setValue(getValue());
		return property;
	}

	public String toString() {
		return String.format("%tF", value);
	}

}
