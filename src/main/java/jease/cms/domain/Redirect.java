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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jfix.db4o.Persistent;

public class Redirect extends Persistent {

	private String source;
	private String target;
	private Date timestamp;
	private transient Pattern pattern;

	public Redirect() {
	}

	public Redirect(String source, String target) {
		this.source = source;
		this.target = target;
		this.timestamp = new Date();
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
		this.pattern = null;
	}

	public String getTarget() {
		return target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}

	public String transform(String input) {
		if (pattern == null) {
			pattern = Pattern.compile(source);
		}
		Matcher matcher = pattern.matcher(input);
		if (matcher.matches()) {
			return matcher.replaceFirst(target);
		} else {
			return input;
		}
	}

	public String toString() {
		return source;
	}

}
