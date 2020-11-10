/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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

import jfix.db4o.Blob;
import jfix.util.Files;

/**
 * A File stores all kinds of binary content as blob in the file-system.
 * Additionally the content-type is stored, so the Id of the File doesn't need
 * to reflect the stored content.
 */
public class File extends Content {

	private String contentType;
	private Blob blob = new Blob();

	public File() {
	}

	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

	/**
	 * Returns true if it is valid to store given content type in object. File
	 * accepts all content types, derived implementations may limit the set of
	 * valid content types.
	 */
	public boolean isValidContentType(String contentType) {
		return true;
	}

	public java.io.File getFile() {
		return blob.getFile();
	}

	public boolean isPage() {
		return false;
	}

	public long getSize() {
		return super.getSize() + getContentType().length()
				+ blob.getFile().length();
	}

	public File copy() {
		File file = (File) super.copy();
		file.setContentType(getContentType());
		Files.copy(getFile(), file.getFile());
		return file;
	}
}
