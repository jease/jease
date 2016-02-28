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
package jfix.zk;

import jfix.util.I18N;

import org.apache.commons.lang3.StringUtils;

public class Fileupload extends org.zkoss.zul.Button {

	public Fileupload() {
		this(I18N.get("Upload"), Images.UserHome);
	}

	public Fileupload(String label, String image) {
		super(label, image);
		setUpload("true");
	}

	public void setUploadLimit(String limit) {
		if (StringUtils.isNotBlank(limit)) {
			setUpload("true,maxsize=" + limit);
		}
	}
}
