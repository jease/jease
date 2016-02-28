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
package jease.cmf.service;

import java.text.Normalizer;

import jfix.util.I18N;

/**
 * Service to ease the normalization of filenames.
 */
public class Filenames {

	private static FilenameConverter filenameConverter = new FilenameConverter();

	public static class FilenameConverter {
		public String convert(String filename) {
			if (filename == null) {
				return null;
			}
			for (String codePair : I18N.get("ASCII_CODES").split(",")) {
				if (codePair.contains(":")) {
					String[] codePairArray = codePair.split(":");
					filename = filename.replace(codePairArray[0],
							codePairArray[1]);
				}
			}
			return Normalizer.normalize(filename, Normalizer.Form.NFD)
					.replaceAll("[\\p{InCombiningDiacriticalMarks}]+", "")
					.replaceAll("[^a-zA-Z0-9_/.-]", "-");
		}
	}

	public static void setFilenameConverter(FilenameConverter filenameConverter) {
		Filenames.filenameConverter = filenameConverter;
	}

	/**
	 * Converts filename into id by replacing all non-ASCII-chars with ASCII
	 * characters and underscores.
	 */
	public static String asId(String filename) {
		return filenameConverter.convert(filename);
	}

}
