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

import java.util.Random;

import jfix.db4o.Persistent;

/**
 * Type safe base class for persistent value objects with a name which needs to
 * be extended to store values.
 */
public abstract class Property extends Persistent implements Persistent.Value {

	private int serial;
	private String name;

	public Property() {
	}

	public Property(String name) {
		setName(name);
	}

	public int getSerial() {
		return serial;
	}

	public void initSerial() {
		do {
			serial = new Random().nextInt();
		} while (serial < 1);
	}

	public void setSerial(int serial) {
		this.serial = serial;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return getClass().getSimpleName().replace(
				Property.class.getSimpleName(), "");
	}

	public long getSize() {
		return String.valueOf(toString()).length();
	}

	public String toString() {
		return name;
	}

	public void cloneTo(Property clone) {
		clone.setName(getName());
	}

	public void replace(String target, String replacement) {
		if (getName() != null) {
			setName(getName().replace(target, replacement));
		}
	}

	public Property copy() {
		try {
			Property property = getClass().newInstance();
			property.setSerial(getSerial());
			property.setName(getName());
			return property;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}
}
