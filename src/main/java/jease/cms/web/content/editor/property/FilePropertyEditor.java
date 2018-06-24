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
package jease.cms.web.content.editor.property;

import jease.Names;
import jease.Registry;
import jease.cms.domain.property.FileProperty;
import jfix.zk.Mediafield;

public class FilePropertyEditor extends Mediafield implements
		PropertyEditor<FileProperty> {

	private FileProperty property;

	public FilePropertyEditor() {
		setUploadLimit(Registry.getParameter(Names.JEASE_UPLOAD_LIMIT));
	}

	public FileProperty getProperty() {
		property.setFilename(getName());
		property.setContentType(getContentType());
		copyToFile(property.getFile());
		return property;
	}

	public void setProperty(FileProperty property) {
		this.property = property;
		setMedia(property.getFilename(), property.getContentType(),
				property.getFile());
	}

}
