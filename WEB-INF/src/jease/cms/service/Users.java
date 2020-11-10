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
package jease.cms.service;

import java.util.List;

import jease.cms.domain.User;
import jfix.db4o.Database;
import jfix.functor.Predicate;

public class Users {

	public static List<User> queryAdministrators() {
		return Database.query(User.class, new Predicate<User>() {
			public boolean test(User user) {
				return user.isAdministrator();
			}
		});
	}

	public static User queryByLogin(final String login, final String password) {
		return Database.queryUnique(User.class, new Predicate<User>() {
			public boolean test(User user) {
				return login.equals(user.getLogin())
						&& password.equals(user.getPassword());
			}
		});
	}

}
