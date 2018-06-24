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
package jease.cms.web.system.redirect;

import java.util.List;

import jease.cms.domain.Redirect;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<Redirect> {

	public Redirect newObject() {
		return new Redirect();
	}

	public String[] getColumns() {
		return new String[] { I18N.get("Date"), I18N.get("Source"),
				I18N.get("Target") };
	}

	public int[] getProportions() {
		return new int[] { 1, 3, 3 };
	}

	public List<Redirect> getList() {
		return Database.query(Redirect.class);
	}

	public Object getValue(Redirect redirect, int column) {
		switch (column) {
		case 0:
			return redirect.getTimestamp();
		case 1:
			return redirect.getSource();
		case 2:
			return redirect.getTarget();
		}
		return "";
	}
}