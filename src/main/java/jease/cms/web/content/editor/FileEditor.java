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
package jease.cms.web.content.editor;

import jease.Names;
import jease.Registry;
import jease.cms.domain.File;
import jfix.util.I18N;
import jfix.zk.Mediafield;
import jfix.zk.Modal;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Events;

public class FileEditor<E extends File> extends ContentEditor<E> {

	Mediafield media = new Mediafield();

	public FileEditor() {
		media.setHeight((100 + getDesktopHeight() / 3) + "px");
		media.addEventListener(Events.ON_UPLOAD, evt -> {
			if (media.getMedia() != null) {
				uploadPerformed();
			}
		});
		media.setUploadLimit(Registry.getParameter(Names.JEASE_UPLOAD_LIMIT));
	}

	public void init() {
		add(I18N.get("File"), media);
	}

	public void load() {
		media.setMedia(getNode().getId(), getNode().getContentType(), getNode()
				.getFile());
	}

	public void save() {
		getNode().setContentType(media.getContentType());
		media.copyToFile(getNode().getFile());
	}

	public void validate() {
		validate(media.isEmpty(), I18N.get("File_is_required"));
	}

	protected void uploadPerformed() {
		if (getObject().isValidContentType(media.getContentType())) {
			String filename = media.getName();
			if (StringUtils.isEmpty(id.getValue())) {
				id.setText(filename);
			}
			if (StringUtils.isEmpty(title.getValue())) {
				title.setText(FilenameUtils.removeExtension(filename));
			}
		} else {
			Modal.error(I18N.get("Content_is_not_valid"));
			media.setMedia(null);
			refresh();
		}
	}

}
