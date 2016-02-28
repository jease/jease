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
package jease.cms.web.content;

import java.io.File;

import jease.Names;
import jease.Registry;
import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.User;
import jease.cms.service.Backups;
import jease.cms.service.Imports;
import jfix.util.I18N;
import jfix.zk.Filedownload;
import jfix.zk.Fileupload;
import jfix.zk.Images;
import jfix.zk.Medias;
import jfix.zk.Modal;
import jfix.zk.WebBrowser;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;

/**
 * JeaseCMS with additional content dump/restore and fileupload for quick
 * content import. Dump/Restore is only available for administrators to avoid
 * users importing scripts via restore-operation.
 */
public class ContentManager extends Jease {

	private Button view;
	private Button dump;
	private Fileupload restore;
	private Fileupload upload;

	public ContentManager() {
		getFlatTable().getLeftbox().getChildren().clear();
		Component container = getTreeTable().getRightbox();
		container.getChildren().clear();
		if (JeaseSession.get(User.class).isAdministrator()) {
			initDumpButton();
			initRestoreButton();
			container.appendChild(dump);
			container.appendChild(restore);
		}
		initUploadButton();
		container.appendChild(upload);
		if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
			initViewButton();
			container.appendChild(view);
		}
	}

	private void initDumpButton() {
		dump = new Button(I18N.get("Dump"), Images.DriveCdrom);
		dump.addEventListener(Events.ON_CLICK, event -> Filedownload
				.save(Backups.dump(JeaseSession.getContainer()))

		);
	}

	private void initRestoreButton() {
		restore = new Fileupload(I18N.get("Restore"), Images.MediaCdrom);
		restore.addEventListener(
				Events.ON_UPLOAD,
				event -> {
					Media media = ((UploadEvent) event).getMedia();
					if (media != null) {
						try {
							File backupFile = Medias.asFile(media);
							backupFile.deleteOnExit();
							Backups.restore(backupFile,
									JeaseSession.getContainer(),
									JeaseSession.get(User.class));
						} catch (Exception e) {
							Modal.error(e.getMessage());
						} finally {
							refresh();
						}
					}
				});
	}

	private void initUploadButton() {
		upload = new Fileupload(I18N.get("Upload"), Images.UserHome);
		upload.addEventListener(
				Events.ON_UPLOAD,
				event -> {
					Media media = ((UploadEvent) event).getMedia();
					if (media != null) {
						try {
							File inputFile = Medias.asFile(media);
							inputFile.deleteOnExit();
							Imports.fromFile(inputFile,
									JeaseSession.getContainer(),
									JeaseSession.get(User.class));
						} catch (Exception e) {
							Modal.error(e.getMessage());
						} finally {
							refresh();
						}
					}
				});
		upload.setUploadLimit(Registry.getParameter(Names.JEASE_UPLOAD_LIMIT));
	}

	private void initViewButton() {
		view = new Button(I18N.get("View"), Images.InternetWebBrowser);
		view.addEventListener(
				Events.ON_CLICK,
				event -> {
					getRoot().appendChild(
							new WebBrowser(JeaseSession.getContainer()
									.getPath()));
				});
	}

	public void refresh() {
		super.refresh();
		if (upload != null) {
			upload.setUploadLimit(Registry
					.getParameter(Names.JEASE_UPLOAD_LIMIT));
		}
	}
}
