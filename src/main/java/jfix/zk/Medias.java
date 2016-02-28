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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;

import org.apache.commons.io.IOUtils;
import org.zkoss.util.media.Media;

public class Medias {

	public static InputStream asStream(Media media) {
		return new BufferedInputStream(
				media.inMemory() ? new ByteArrayInputStream(media.getByteData())
						: media.getStreamData());
	}

	public static Reader asReader(Media media) {
		return new BufferedReader(media.inMemory() ? new StringReader(
				media.getStringData()) : media.getReaderData());
	}

	public static File asFile(Media media) {
		try {
			File directory = Files.createTempDirectory("jfix-media").toFile();
			directory.deleteOnExit();
			File file = new File(directory.getPath() + File.separator
					+ media.getName());
			OutputStream output = new FileOutputStream(file);
			if (media.isBinary()) {
				InputStream input = Medias.asStream(media);
				IOUtils.copy(input, output);
				input.close();
			} else {
				Reader input = Medias.asReader(media);
				IOUtils.copy(input, output);
				input.close();
			}
			output.close();
			return file;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	public static String asString(Media media) {
		try {
			String result = null;
			if (media.isBinary()) {
				InputStream input = Medias.asStream(media);
				result = IOUtils.toString(input, "UTF-8");
				input.close();
			} else {
				Reader reader = Medias.asReader(media);
				result = IOUtils.toString(reader);
				reader.close();
			}
			return result;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

}
