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
package jfix.db4o;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * Persistent class which can be used to store binary content directly in
 * file-system. You can get access to the file by calling #getFile().
 * 
 * Initially the blob is stored in #JAVA_IO_TMPDIR. When the blob is persisted
 * in database for the first time, the file will be moved to a directory
 * dependent on the database by calling #initPath().
 */
public class Blob extends Persistent implements Persistent.Value {

	protected static transient String JAVA_IO_TMPDIR = System
			.getProperty("java.io.tmpdir") + File.separator;

	protected transient String path;
	protected final String id = UUID.randomUUID().toString();

	public String getId() {
		return id;
	}

	public File getFile() {
		return new File(path != null ? path : (JAVA_IO_TMPDIR + id));
	}

	public String toString() {
		return id;
	}

	protected void initPath(String blobDirectory) {
		if (path == null) {
			StringBuilder sb = new StringBuilder();
			sb.append(blobDirectory).append("blob").append(File.separator);
			sb.append(id.substring(0, 2)).append(File.separator);
			sb.append(id.substring(2, 4)).append(File.separator);
			new File(sb.toString()).mkdirs();
			File source = getFile();
			path = sb.append(id).toString();
			File target = getFile();
			if (source.exists() && !target.exists()) {
				try {
					Files.move(source.toPath(), target.toPath(),
							StandardCopyOption.REPLACE_EXISTING);
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

}
