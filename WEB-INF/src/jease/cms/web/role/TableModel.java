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
package jease.cms.web.role;

import java.util.List;

import jease.cms.domain.Role;
import jease.cms.web.i18n.Strings;
import jfix.db4o.Database;
import jfix.functor.Function;
import jfix.functor.Functors;
import jfix.util.Arrays;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<Role> {

	public Role newObject() {
		return new Role();
	}

	public String[] getColumns() {
		return new String[] { Strings.Name, Strings.Type };
	}

	public int[] getProportions() {
		return new int[] { 1, 4 };
	}

	public List<Role> getList() {
		return Database.query(Role.class);
	}

	public Object getValue(Role role, int column) {
		switch (column) {
		case 0:
			return role.getName();
		case 1:
			return Arrays.join(Functors.transform(role.getTypes(),
					new Function<String, String>() {
						public String evaluate(String str) {
							return str.substring(str.lastIndexOf(".") + 1);
						}
					}), " | ");
		}
		return "";
	}

}