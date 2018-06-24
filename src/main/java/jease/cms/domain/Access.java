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
package jease.cms.domain;

import java.util.Date;

import jfix.util.Crypts;

import org.apache.commons.lang3.StringUtils;

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
	private Date start;
	private Date stop;

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
		return StringUtils.equals(this.password, encrypt(password));
	}

	public void setPassword(String password) {
		if (!StringUtils.equals(this.password, password)) {
			this.password = encrypt(password);
		}
	}

	protected String encrypt(String input) {
		return StringUtils.isBlank(input) ? input : Crypts
				.md5(input.getBytes());
	}

	public Date getStart() {
		return start;
	}

	public void setStart(Date start) {
		this.start = start;
	}

	public Date getStop() {
		return stop;
	}

	public void setStop(Date stop) {
		this.stop = stop;
	}

	public boolean isGuarding() {
		if (start == null && stop == null) {
			return true;
		}
		Date now = new Date();
		if (start != null && stop != null) {
			if (start.before(stop)) {
				return start.before(now) && stop.after(now);
			} else {
				return start.before(now) || stop.after(now);
			}
		}
		if (start != null && stop == null) {
			return start.before(now);
		}
		if (start == null && stop != null) {
			return stop.after(now);
		}
		return false;
	}

	public boolean approves(String login, String password) {
		return StringUtils.equals(getLogin(), login) && hasPassword(password);
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

	public Access copy(boolean recursive) {
		Access access = (Access) super.copy(recursive);
		access.setLogin(getLogin());
		access.setPassword(getPassword());
		return access;
	}
}