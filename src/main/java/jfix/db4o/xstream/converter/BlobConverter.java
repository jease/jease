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
package jfix.db4o.xstream.converter;

import java.io.File;

import jfix.db4o.Blob;

import org.apache.commons.io.FileUtils;

import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.core.util.Base64Encoder;

public class BlobConverter implements SingleValueConverter {

	public boolean canConvert(Class clazz) {
		return Blob.class.isAssignableFrom(clazz);
	}

	public String toString(Object obj) {
		try {
			File file = ((Blob) obj).getFile();
			if (file.exists()) {
				return new Base64Encoder().encode(FileUtils
						.readFileToByteArray(file));
			} else {
				return "";
			}
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public Object fromString(String str) {
		Blob blob = new Blob();
		try {
			FileUtils.writeByteArrayToFile(blob.getFile(),
					new Base64Encoder().decode(str));
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
		return blob;
	}

}
