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
import jfix.util.Crypts;
import jfix.util.Validations;

/**
 * Users can create, update and delete content within the CMS. A user has a role
 * which determines which content types a user can create. A user has one or
 * more declared roots which are the folders which the user can access to
 * manipulate content.
 */
public class User extends Persistent {

	private String name;
	private String login;
	private String password;
	private String email;
	private Folder[] roots;
	private Role role;
	@Deprecated
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

	public boolean hasPassword(String password) {
		return Validations.equals(this.password, encrypt(password));
	}

	public void setPassword(String password) {
		if (!Validations.equals(this.password, password)) {
			this.password = encrypt(password);
		}
	}

	protected String encrypt(String input) {
		return Validations.isEmpty(input) ? input : Crypts
				.md5(input.getBytes());
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

	public void setRole(Role role) {
		this.role = role;
	}

	public Role getRole() {
		return role;
	}

	public boolean isAdministrator() {
		return role != null ? role.isAdministrator() : false;
	}

	public String toString() {
		return "" + login;
	}
}
