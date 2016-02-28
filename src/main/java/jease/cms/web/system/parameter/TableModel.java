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
package jease.cms.web.system.parameter;

import java.util.List;

import jease.cms.domain.Parameter;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;
import jfix.zk.View;

import org.zkoss.zul.Label;

public class TableModel extends ObjectTableModel<Parameter> {

	public Parameter newObject() {
		return new Parameter();
	}

	public String[] getColumns() {
		return new String[] { I18N.get("Key"), I18N.get("Value") };
	}

	public int[] getProportions() {
		return new int[] { 1, 1 };
	}

	public List<Parameter> getList() {
		return Database.query(Parameter.class);
	}

	public Object getValue(Parameter parameter, int column) {
		switch (column) {
		case 0:
			return parameter.getKey();
		case 1:
			String value = parameter.getValue();
			int index = value.indexOf("\n");
			if (index != -1) {
				return new View<String>(value, new Label(value.substring(0,
						index) + " [...]"));
			} else {
				return parameter.getValue();
			}
		}
		return "";
	}

}