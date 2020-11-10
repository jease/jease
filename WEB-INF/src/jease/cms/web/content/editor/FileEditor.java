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
package jease.cms.web.content.editor;

import jease.cms.domain.File;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Mediafield;

import org.zkoss.zk.ui.event.Event;

public class FileEditor extends ContentEditor<File> {

	Mediafield file = new Mediafield();

	public FileEditor() {
		file.setHeight("200px");
		file.addUploadListener(new ActionListener() {
			public void actionPerformed(Event event) {
				if (file.getMedia() != null) {
					uploadPerformed();
				}
			}
		});
	}

	public void init() {
		add(Strings.File, file);
	}

	public void load() {
		file.setMedia(getNode().getId(), getNode().getContentType(), getNode()
				.getFile());
	}

	public void save() {
		getNode().setContentType(file.getContentType());
		file.copyToFile(getNode().getFile());
	}

	public void validate() {
		if (file.isEmpty()) {
			addError(Strings.File_is_required);
		}
	}

	protected void uploadPerformed() {
		String filename = file.getName();
		if (id.isEmpty()) {
			id.setText(filename.replace(" ", "_"));
		}
		if (title.isEmpty()) {
			int extension = filename.lastIndexOf('.');
			if (extension != -1) {
				title.setText(filename.substring(0, extension));
			} else {
				title.setText(filename);
			}
		}
	}

}
