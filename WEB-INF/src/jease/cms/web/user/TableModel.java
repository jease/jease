/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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
package jease.cms.web.user;

import java.util.List;

import jease.cms.domain.User;
import jease.cms.web.i18n.Strings;
import jfix.db4o.Database;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<User> {

	public User newObject() {
		return new User();
	}

	public String[] getColumns() {
		return new String[] { Strings.Login, Strings.Name, Strings.Roots,
				Strings.Administrator };
	}

	public List<User> getList() {
		return Database.query(User.class);
	}

	public Object getValue(User user, int column) {
		switch (column) {
		case 0:
			return user.getLogin();
		case 1:
			return user.getName();
		case 2:
			return user.getRoots();
		case 3:
			return user.isAdministrator() ? Strings.Yes : Strings.No;
		}
		return "";
	}

}