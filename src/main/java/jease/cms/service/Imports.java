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
import jease.cms.domain.Document;
import jease.cms.domain.File;
import jease.cms.domain.Folder;
import jease.cms.domain.Image;
import jease.cms.domain.Text;
import jease.cms.domain.User;
import jfix.util.MimeTypes;
import jfix.util.Urls;
import jfix.util.Zipfiles;
import jfix.util.Zipfiles.EntryHandler;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Service to import single files or whole file-systems (as zipped files which
 * will be automatically unzipped) into Jease.
 */
public class Imports {

	public static void fromFile(final java.io.File file, final Node parent,
			final User editor) throws Exception {
		if (MimeTypes.guessContentTypeFromName(file.getName()).equals(
				"application/zip")) {
			final StringBuilder errors = new StringBuilder();
			Zipfiles.unzip(file, new EntryHandler() {
				public void process(String path, String entryName,
						InputStream inputStream) throws Exception {
					try {
						if (StringUtils.isNotBlank(path)) {
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
				if (editor.getRole().containsType(newFolder.getClass())) {
					Nodes.append(
							relativeRoot.getChild(Filenames.asId(parentPath)),
							newFolder);
				} else {
					throw new NodeException.IllegalNesting();
				}
			} else {
				Folder newFolder = newFolder(path);
				newFolder.setEditor(editor);
				if (editor.getRole().containsType(newFolder.getClass())) {
					Nodes.append(relativeRoot, newFolder);
				} else {
					throw new NodeException.IllegalNesting();
				}
			}
		} else {
			if (!(node instanceof Folder)) {
				throw new NodeException.IllegalNesting();
			}
		}
	}

	public static void fromInputStream(String filename,
			InputStream inputStream, Node parent, User editor) throws Exception {
		Content newContent = newContent(filename, inputStream, editor);
		newContent.setEditor(editor);
		Nodes.append(parent, Contents.customize(newContent));
	}

	private static Content newContent(String filename, InputStream inputStream,
			User editor) throws NodeException, IOException {
		String contentType = MimeTypes.guessContentTypeFromName(filename);
		if (editor.getRole().containsType(Text.class)
				&& contentType.startsWith("text/html")) {
			return newText(filename, inputStream);
		} else if (editor.getRole().containsType(Image.class)
				&& contentType.startsWith("image/")) {
			return newImage(filename, inputStream);
		} else if (editor.getRole().containsType(Document.class)
				&& filename
						.matches(".+\\.(csv|doc|docx|odp|ods|odt|pdf|ppt|pps|rtf|sxi|sxw|sxc|txt|xls|xlsx)$")) {
			return newDocument(filename, inputStream);
		} else if (editor.getRole().containsType(File.class)) {
			return newFile(filename, inputStream);
		} else {
			throw new NodeException.IllegalNesting();
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
		text.setTitle(FilenameUtils.removeExtension(filename));
		text.setLastModified(new Date());
		text.setContent(IOUtils.toString(inputStream, "UTF-8"));
		text.setPlain(true);
		return text;
	}

	private static Image newImage(String filename, InputStream inputStream)
			throws IOException {
		Image image = new Image();
		image.setId(Filenames.asId(filename));
		image.setTitle(FilenameUtils.removeExtension(filename));
		image.setLastModified(new Date());
		image.setContentType(MimeTypes.guessContentTypeFromName(filename));
		copyStreamToFile(inputStream, image.getFile());
		return image;
	}

	private static Document newDocument(String filename, InputStream inputStream)
			throws IOException {
		Document document = new Document();
		document.setId(Filenames.asId(filename));
		document.setTitle(FilenameUtils.removeExtension(filename));
		document.setLastModified(new Date());
		document.setContentType(MimeTypes.guessContentTypeFromName(filename));
		copyStreamToFile(inputStream, document.getFile());
		// Trigger conversion to plain text
		document.getText();
		return document;
	}

	private static File newFile(String filename, InputStream inputStream)
			throws IOException {
		File file = new File();
		file.setId(Filenames.asId(filename));
		file.setTitle(FilenameUtils.removeExtension(filename));
		file.setLastModified(new Date());
		file.setContentType(MimeTypes.guessContentTypeFromName(filename));
		copyStreamToFile(inputStream, file.getFile());
		return file;
	}

	private static void copyStreamToFile(InputStream inputStream,
			java.io.File file) throws IOException {
		OutputStream outputStream = new FileOutputStream(file);
		IOUtils.copy(inputStream, outputStream);
		outputStream.close();
	}
}
