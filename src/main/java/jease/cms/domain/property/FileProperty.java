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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import jfix.db4o.Blob;

public class FileProperty extends Property {

	private String filename;
	private String contentType;
	private Blob blob = new Blob();

	public FileProperty() {
	}

	public FileProperty(String name) {
		super(name);
	}

	public FileProperty(String name, String filename, String contentType) {
		this(name);
		setFilename(filename);
		setContentType(contentType);
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}

	public String getContentType() {
		return contentType;
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public Blob getBlob() {
		return blob;
	}

	public java.io.File getFile() {
		return blob.getFile();
	}

	public long getSize() {
		return super.getSize() + blob.getFile().length();
	}

	public FileProperty copy() {
		FileProperty property = (FileProperty) super.copy();
		property.setFilename(getFilename());
		property.setContentType(getContentType());
		try {
			Files.copy(getFile().toPath(), property.getFile().toPath(),
					StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return property;
	}

	public String toString() {
		return filename;
	}

}
