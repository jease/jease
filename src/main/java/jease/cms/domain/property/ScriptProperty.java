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
package jease.cms.domain.property;

import jfix.util.Reflections;

import org.apache.commons.lang3.StringUtils;

public class ScriptProperty extends Property implements Provider {

	private String code;
	private transient Provider provider;

	public ScriptProperty() {
	}

	public ScriptProperty(String name) {
		super(name);
	}

	public ScriptProperty(String name, String value) {
		this(name);
		setCode(value);
	}

	public String[] getValue() {
		try {
			if (provider == null) {
				provider = (Provider) Reflections.newInstance(code);
			}
			return provider.getValue();
		} catch (Throwable e) {
			return new String[] { e.getMessage() };
		}
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
		this.provider = null;
	}

	public ScriptProperty copy() {
		ScriptProperty property = (ScriptProperty) super.copy();
		property.setCode(getCode());
		return property;
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		if (getCode() != null) {
			setCode(getCode().replace(target, replacement));
		}
	}

	public String toString() {
		return StringUtils.join(getValue(), "\n");
	}
}
