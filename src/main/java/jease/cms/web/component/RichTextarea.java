package jease.cms.web.component;

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

import jease.Names;
import jease.Registry;

import org.zkforge.ckez.CKeditor;

public class RichTextarea extends CKeditor {

	public static String FILEBROWSER_BROWSE_URL = "~./jfix/ckez/browse.zul";

	public RichTextarea() {
		super();
		if (FILEBROWSER_BROWSE_URL != null) {
			setFilebrowserBrowseUrl(FILEBROWSER_BROWSE_URL);
		}
		setWidth("100%");
		setHeight("300px");
		setFileBrowserTemplate("~./jease/ckez/browse.zul");
		String path = Registry.getParameter(Names.JEASE_CKEDITOR_PATH);
		if (path != null) {
			setCustomConfigurationsPath(path);
		} else {
			setCustomConfigurationsPath("/cms/config/ckeditor.js");
		}

	}

	public void setText(Object text) {
		if (text == null) {
			super.setValue("");
		} else {
			super.setValue(String.valueOf(text));
		}
	}

	public String getText() {
		return getValue();
	}

	public boolean isEmpty() {
		return "".equals(getValue().trim());
	}

}
