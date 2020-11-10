/*
    Copyright (C) 2011 maik.jablonski@gmail.com

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
import jease.cms.web.i18n.Strings;
import jfix.db4o.Database;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<Parameter> {

	public Parameter newObject() {
		return new Parameter();
	}

	public String[] getColumns() {
		return new String[] { Strings.Key, Strings.Value };
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
			return parameter.getValue();
		}
		return "";
	}

}