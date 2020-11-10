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
package jease.cms.domain;

import jfix.db4o.Persistent;

/**
 * Users can create, update and delete content within the CMS. A user has one or
 * more declared roots which are the folders which the user can access to
 * manipulate content. Only administrators are allowed to create other users and
 * can create privileged content-types.
 */
public class User extends Persistent {

	private String name;
	private String login;
	private String password;
	private String email;
	private Folder[] roots;
	private boolean administrator;

	public User() {
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setRoots(Folder[] roots) {
		this.roots = roots;
	}

	/**
	 * Returns all virtual "root"-folders which can be accessed by the user via
	 * the CMS.
	 */
	public Folder[] getRoots() {
		return roots;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public boolean isAdministrator() {
		return administrator;
	}

	public void setAdministrator(boolean administrator) {
		this.administrator = administrator;
	}

	public String toString() {
		return "" + login;
	}
}
