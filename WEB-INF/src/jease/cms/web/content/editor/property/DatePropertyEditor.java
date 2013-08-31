/*
    Copyright (C) 2013 maik.jablonski@jease.org

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
package jease.cms.web.content.editor.property;

import jease.cms.domain.property.DateProperty;
import jfix.zk.Datefield;

public class DatePropertyEditor extends Datefield implements
		PropertyEditor<DateProperty> {

	private DateProperty property;

	public DateProperty getProperty() {
		property.setValue(getValue());
		return property;
	}

	public void setProperty(DateProperty property) {
		this.property = property;
		setValue(property.getValue());
	}

}
