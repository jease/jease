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

import java.io.File;
import java.io.FileNotFoundException;

import jfix.util.MimeTypes;
import jfix.util.Urls;

public class Filedownload extends org.zkoss.zul.Filedownload {

	public static void save(File file) {
		try {
			save(file, MimeTypes.guessContentTypeFromName(file.getName()));
		} catch (FileNotFoundException e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static void save(String filename, byte[] content) {
		save(content, MimeTypes.guessContentTypeFromName(filename), filename);
	}

}
