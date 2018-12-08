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
package jease.cms.web.system.role;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jease.cms.domain.Role;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<Role> {

    public Role newObject() {
        return new Role();
    }

    public String[] getColumns() {
        return new String[] { I18N.get("Name"), I18N.get("Type") };
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
            return Stream
                    .of(role.getTypes())
                    .map($str -> I18N.get($str.substring($str.lastIndexOf(".") + 1)))
                    .collect(Collectors.joining(" | "));
        }
        return "";
    }

}