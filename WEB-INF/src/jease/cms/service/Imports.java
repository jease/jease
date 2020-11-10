/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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
package jease.cms.service;

import java.io.*;
import java.util.*;

import jease.cmf.domain.*;
import jease.cmf.service.*;
import jease.cms.domain.*;
import jease.cms.domain.File;
import jfix.util.*;
import jfix.util.Zipfiles.*;

import org.apache.commons.io.*;

public class Imports {

	public static void fromFile(final String filename, final java.io.File file,
			final Node parent, final User editor) throws Exception {
		if (Filenames.asContentType(filename).equals("application/zip")) {
			final StringBuilder errors = new StringBuilder();
			Zipfiles.unzip(file, new EntryHandler() {
				public void process(String path, String entryName,
						InputStream inputStream) throws Exception {
					try {
						makeFolders(parent, path, editor);
						if (inputStream != null) {
							Imports.fromInputStream(entryName, inputStream,
									parent.getChild(Filenames.asId(path)),
									editor);
						}
					} catch (NodeException e) {
						errors.append(e.getMessage()).append(": ").append(path)
								.append("/").append(entryName).append("\n");
					}
				}
			});
			if (errors.length() != 0) {
				throw new Exception(errors.toString());
			}
		} else {
			Imports.fromInputStream(filename, new FileInputStream(file),
					parent, editor);
		}
	}

	private static void makeFolders(Node relativeRoot, String path, User editor)
			throws NodeException {
		if (relativeRoot.getChild(Filenames.asId(path)) == null) {
			int lastSlash = path.lastIndexOf("/");
			if (lastSlash != -1) {
				String parentPath = path.substring(0, lastSlash);
				String filename = path.substring(lastSlash + 1, path.length());
				makeFolders(relativeRoot, parentPath, editor);
				Folder newFolder = newFolder(filename);
				newFolder.setEditor(editor);
				Nodes.append(relativeRoot.getChild(Filenames.asId(parentPath)),
						newFolder);
			} else {
				Folder newFolder = newFolder(path);
				newFolder.setEditor(editor);
				Nodes.append(relativeRoot, newFolder);
			}
		}
	}

	public static void fromInputStream(String filename,
			InputStream inputStream, Node parent, User editor) throws Exception {
		Content newContent = newContent(filename, inputStream);
		newContent.setEditor(editor);
		Nodes.append(parent, newContent);
	}

	private static Content newContent(String filename, InputStream inputStream)
			throws NodeException, IOException {
		String contentType = Filenames.asContentType(filename);
		if (contentType.startsWith("text/html")) {
			return newText(filename, inputStream);
		} else if (contentType.startsWith("image")) {
			return newImage(filename, inputStream);
		} else {
			return newFile(filename, inputStream);
		}
	}

	private static Folder newFolder(String filename) {
		Folder folder = new Folder();
		folder.setId(Filenames.asId(filename));
		folder.setTitle(filename);
		folder.setLastModified(new Date());
		return folder;
	}

	private static Text newText(String filename, InputStream inputStream)
			throws IOException {
		Text text = new Text();
		text.setId(Filenames.asId(filename));
		text.setTitle(Filenames.asTitle(filename));
		text.setLastModified(new Date());
		text.setContent(IOUtils.toString(inputStream, "UTF-8"));
		text
				.setPlain(!(filename.endsWith(".htm") || filename
						.endsWith(".html")));
		return text;
	}

	private static Image newImage(String filename, InputStream inputStream)
			throws IOException {
		Image image = new Image();
		image.setId(Filenames.asId(filename));
		image.setTitle(Filenames.asTitle(filename));
		image.setLastModified(new Date());
		image.setContentType(Filenames.asContentType(filename));
		IOUtils.copy(inputStream, new FileOutputStream(image.getFile()));
		return image;
	}

	private static File newFile(String filename, InputStream inputStream)
			throws IOException {
		File file = new File();
		file.setId(Filenames.asId(filename));
		file.setTitle(Filenames.asTitle(filename));
		file.setLastModified(new Date());
		file.setContentType(Filenames.asContentType(filename));
		IOUtils.copy(inputStream, new FileOutputStream(file.getFile()));
		return file;
	}

}
