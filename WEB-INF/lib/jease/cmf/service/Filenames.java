/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import jfix.util.Urls;

/**
 * Service methods to ease the handling of path- and filenames.
 */
public class Filenames {

	/**
	 * Extracts the filename from a given pathname.
	 */
	public static String asFilename(String pathname) {
		if (pathname != null) {
			return new File(pathname).getName();
		} else {
			return null;
		}
	}

	/**
	 * Converts a pathname into an platform-independent URI.
	 */
	public static String asURI(String pathname) {
		if (pathname != null) {
			return new File(pathname).toURI().toString();
		} else {
			return null;
		}
	}

	/**
	 * Converts filename into id by replacing all non-ASCII-chars with
	 * underscores.
	 */
	public static String asId(String filename) {
		return filename.replaceAll("[^a-zA-Z0-9_/.-]", "_");
	}

	/**
	 * Converts filename into extension.
	 */
	public static String asExtension(String filename) {
		return Urls.getExtension(filename);
	}
	
	/**
	 * Converts filename into title by removing extension.
	 */
	public static String asTitle(String filename) {
		int extension = filename.lastIndexOf(".");
		if (extension > 0) {
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

	/**
	 * Returns a recursive list of URIs (pathnames) for all files contained in
	 * given directory.
	 */
	public static List<String> getPathnames(String directory) {
		List<String> result = new ArrayList<String>();
		File file = new File(directory);
		if (file.isFile()) {
			result.add(file.toURI().toString());
		}
		if (file.isDirectory()) {
			for (File child : file.listFiles()) {
				result.addAll(getPathnames(child.getPath()));
			}
		}
		return result;
	}
}
