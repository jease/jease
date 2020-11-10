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
package jease.cms.service;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Filenames;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.File;
import jease.cms.domain.Folder;
import jease.cms.domain.Image;
import jease.cms.domain.Text;
import jease.cms.domain.User;
import jfix.util.Validations;
import jfix.util.Zipfiles;
import jfix.util.Zipfiles.EntryHandler;

import org.apache.commons.io.IOUtils;

/**
 * Service to import single files or whole file-systems (as zipped files which
 * will be automatically unzipped) into Jease.
 */
public class Imports {

	public static void fromFile(final java.io.File file, final Node parent,
			final User editor) throws Exception {
		if (Filenames.asContentType(file.getName()).equals("application/zip")) {
			final StringBuilder errors = new StringBuilder();
			Zipfiles.unzip(file, new EntryHandler() {
				public void process(String path, String entryName,
						InputStream inputStream) throws Exception {
					try {
						if (Validations.isNotEmpty(path)) {
							makeFolders(parent, path, editor);
						}
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
			InputStream inputStream = new FileInputStream(file);
			Imports.fromInputStream(file.getName(), inputStream, parent, editor);
			inputStream.close();
		}
	}

	private static void makeFolders(Node relativeRoot, String path, User editor)
			throws NodeException {
		Node node = relativeRoot.getChild(Filenames.asId(path));
		if (node == null) {
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
		} else {
			if (!(node instanceof Folder)) {
				throw new NodeException.IllegalNesting();
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
		text.setContent(readString(inputStream));
		text.setPlain(true);
		return text;
	}

	private static Image newImage(String filename, InputStream inputStream)
			throws IOException {
		Image image = new Image();
		image.setId(Filenames.asId(filename));
		image.setTitle(Filenames.asTitle(filename));
		image.setLastModified(new Date());
		image.setContentType(Filenames.asContentType(filename));
		copyStreamToFile(inputStream, image.getFile());
		return image;
	}

	private static File newFile(String filename, InputStream inputStream)
			throws IOException {
		File file = new File();
		file.setId(Filenames.asId(filename));
		file.setTitle(Filenames.asTitle(filename));
		file.setLastModified(new Date());
		file.setContentType(Filenames.asContentType(filename));
		copyStreamToFile(inputStream, file.getFile());
		return file;
	}

	private static String readString(InputStream inputStream)
			throws IOException {
		return IOUtils.toString(inputStream, "UTF-8");
	}

	private static void copyStreamToFile(InputStream inputStream,
			java.io.File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();
	}
}
