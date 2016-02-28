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
package jease.cms.web.user;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import jease.cms.domain.User;
import jease.cms.service.Users;
import jease.cms.web.servlet.JeaseSessionListener;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;
import jfix.zk.Sessions;

public class TableModel extends ObjectTableModel<User> {

	public User newObject() {
		if (Sessions.get(User.class).isAdministrator()) {
			return new User();
		} else {
			return null;
		}
	}

	public String[] getColumns() {
		return new String[] { I18N.get("Username"), I18N.get("Name"),
				I18N.get("Email"), I18N.get("Role"), I18N.get("Last_Access"),
				I18N.get("Session"), I18N.get("Roots") };
	}

	public int[] getProportions() {
		return new int[] { 2, 2, 2, 2, 2, 1, 4 };
	}

	public List<User> getList() {
		return Users.queryModifiableByUser(Sessions.get(User.class));
	}

	public Object getValue(User user, int column) {
		switch (column) {
		case 0:
			return user.getLogin();
		case 1:
			return user.getName();
		case 2:
			return user.getEmail();
		case 3:
			return user.getRole();
		case 4:
			long lastAccessedTime = getLastAccessedTime(user);
			return lastAccessedTime != 0 ? new Date(lastAccessedTime) : user
					.getLastSession();
		case 5:
			return getLastAccessedTime(user) != 0 ? I18N.get("Yes") : (user
					.isDisabled() ? I18N.get("Disabled") : I18N.get("No"));
		case 6:
			return user.getRoots();
		}
		return "";
	}

	private long getLastAccessedTime(User user) {
		long lastAccessedTime = 0;
		for (HttpSession session : JeaseSessionListener.getSessions()) {
			if (user.equals(session.getAttribute(user.getClass().toString()))
					&& lastAccessedTime < session.getLastAccessedTime()) {
				lastAccessedTime = session.getLastAccessedTime();
			}
		}
		return lastAccessedTime;
	}
}