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
package jease.cmf.service;

import jfix.util.Urls;

/**
 * Service methods to ease the handling of filenames.
 */
public class Filenames {

	/**
	 * Converts filename into id by replacing spaces with underscores.
	 */
	public static String asId(String filename) {
		return filename.replace(" ", "_");
	}

	/**
	 * Converts filename into title by removing extension.
	 */
	public static String asTitle(String filename) {
		int extension = filename.lastIndexOf(".");
		if (extension != -1) {
			return filename.substring(0, extension);
		} else {
			return filename;
		}
	}

	/**
	 * Converts filename into content-type.
	 */
	public static String asContentType(String filename) {
		return Urls.guessContentTypeFromName(filename);
	}
}
