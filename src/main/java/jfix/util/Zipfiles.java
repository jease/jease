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
package jfix.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

/**
 * Common utilitites to un-/zip files.
 */
public class Zipfiles {

	public interface EntryHandler {
		void process(String path, String filename, InputStream inputStream)
				throws Exception;
	}

	/**
	 * Unzips given file by using given EntryHandler as callback for each entry.
	 * The InputStream in EntryHandler is null if the current entry is a
	 * directory.
	 */
	public static void unzip(java.io.File file, EntryHandler entryHandler)
			throws Exception {
		ZipFile zipFile = new ZipFile(file);
		Enumeration<? extends ZipEntry> entries = zipFile.entries();
		while (entries.hasMoreElements()) {
			ZipEntry entry = (ZipEntry) entries.nextElement();

			String entryName = entry.getName();
			if (entryName.endsWith("/")) {
				entryName = entryName.substring(0, entryName.length() - 1);
			}

			int lastSlash = entryName.lastIndexOf("/");

			String path = null;
			String filename = null;

			if (lastSlash == -1) {
				path = "";
				filename = entryName;
			} else {
				path = entryName.substring(0, lastSlash);
				filename = entryName.substring(lastSlash + 1,
						entryName.length());
			}

			InputStream inputStream = zipFile.getInputStream(entry);
			entryHandler.process(path, filename, entry.isDirectory() ? null
					: inputStream);
			inputStream.close();
		}
		zipFile.close();
	}

	/**
	 * Zips the given file.
	 */
	public static File zip(File file) throws IOException {
		String outFilename = file.getAbsolutePath() + ".zip";
		ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
				outFilename));
		FileInputStream in = new FileInputStream(file);
		out.putNextEntry(new ZipEntry(file.getName()));
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.closeEntry();
		in.close();
		out.close();
		return new File(outFilename);
	}

	/**
	 * Unzips the given file.
	 */
	public static File unzip(File file) throws IOException {
		ZipInputStream in = new ZipInputStream(new FileInputStream(file));
		ZipEntry entry = in.getNextEntry();
		String outFilename = entry.getName();
		OutputStream out = new FileOutputStream(outFilename);
		byte[] buf = new byte[4096];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		out.close();
		in.close();
		return new File(outFilename);
	}

}
