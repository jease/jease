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

public class DoubleProperty extends Property {

	private double value;

	public DoubleProperty() {
	}

	public DoubleProperty(String name) {
		super(name);
	}

	public DoubleProperty(String name, double value) {
		this(name);
		setValue(value);
	}

	public double getValue() {
		return value;
	}

	public void setValue(double value) {
		this.value = value;
	}

	public DoubleProperty copy() {
		DoubleProperty property = (DoubleProperty) super.copy();
		property.setValue(getValue());
		return property;
	}

	public String toString() {
		return String.valueOf(value);
	}

}
