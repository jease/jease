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

/**
 * A Script can be used to generate dynamic content via a template engine (e.g.
 * JSP, Groovy, Velocity, Freemarker, ...).
 */
public class Script extends Content {

	private String code;
	private boolean forward;

	public Script() {
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public boolean isForward() {
		return forward;
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isPage() {
		return !isForward();
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getCode());
	}

	public long getSize() {
		return super.getSize() + getCode().length();
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setCode(getCode().replace(target, replacement));
	}

	public Script copy(boolean recursive) {
		Script script = (Script) super.copy(recursive);
		script.setCode(getCode());
		script.setForward(isForward());
		return script;
	}
}
