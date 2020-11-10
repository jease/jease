/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import jfix.util.Crypts;
import jfix.util.Validations;

/**
 * Access-Object which allows to protect containers with a stored combination of
 * login and password.
 * 
 * Please note: the Access-Object itself doesn't enforce any security on its
 * own, but relies on appropriate code in the public view (site).
 */
public class Access extends Content {

	private String login;
	private String password;

	public Access() {
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

	public boolean isPage() {
		return false;
	}

	public long getSize() {
		return super.getSize() + getLogin().length() + getPassword().length();
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(login);
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setLogin(getLogin().replace(target, replacement));
	}

	public Access copy() {
		Access access = (Access) super.copy();
		access.setLogin(getLogin());
		access.setPassword(getPassword());
		return access;
	}
}