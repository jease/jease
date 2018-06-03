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

import jfix.db4o.Blob;
import jfix.db4o.Persistent;

/**
 * A Version stores a content-revision within a blob. Additionally an info about
 * the version can be stored (e.g. the username who created the version).
 */
public class Version extends Persistent implements Persistent.Value {

	private String info;
	private Blob blob;

	public Version(String info, Blob blob) {
		this.info = info;
		this.blob = blob;
	}

	public String getInfo() {
		return info;
	}

	public Blob getBlob() {
		return blob;
	}

	public String toString() {
		String lastModified = String.format("%1$tF %1$tT", new Date(blob
				.getFile().lastModified()));
		if (info != null) {
			return String.format("%s (%s)", lastModified, info);
		} else {
			return lastModified;
		}
	}

}
