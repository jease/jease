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
package jease.cms.web.content;

import jease.Names;
import jease.Registry;
import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.User;
import jease.cms.service.Backups;
import jease.cms.service.Imports;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Filedownload;
import jfix.zk.Fileupload;
import jfix.zk.Images;
import jfix.zk.Medias;
import jfix.zk.Modal;
import jfix.zk.WebBrowser;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;

/**
 * JeaseCMS with additional content dump/restore and fileupload for quick
 * content import. Dump/Restore is only available for administrators to avoid
 * users importing scripts via restore-operation.
 */
public class ContentManager extends Jease {

	public ContentManager() {
		getFlatTable().getLeftbox().getChildren().clear();
		Component container = getTreeTable().getRightbox();
		container.getChildren().clear();
		if (JeaseSession.get(User.class).isAdministrator()) {
			container.appendChild(newDumpButton());
			container.appendChild(newRestoreButton());
		}
		container.appendChild(newUploadButton());
		if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
			container.appendChild(newViewButton());
		}
	}

	private Component newDumpButton() {
		return new Button(I18N.get("Dump"), Images.DriveCdrom,
				new ActionListener() {
					public void actionPerformed(Event evt) {
						Filedownload.save(Backups.dump(JeaseSession
								.getContainer()));
					}
				});
	}

	private Component newRestoreButton() {
		return new Fileupload(I18N.get("Restore"), Images.MediaCdrom,
				new ActionListener() {
					public void actionPerformed(Event evt) {
						Media media = ((UploadEvent) evt).getMedia();
						if (media != null) {
							try {
								Backups.restore(Medias.asFile(media),
										JeaseSession.getContainer(),
										JeaseSession.get(User.class));
							} catch (Exception e) {
								Modal.error(e.getMessage());
							} finally {
								refresh();
							}
						}
					}
				});
	}

	private Component newUploadButton() {
		return new Fileupload(I18N.get("Upload"), Images.UserHome,
				new ActionListener() {
					public void actionPerformed(Event evt) {
						Media media = ((UploadEvent) evt).getMedia();
						if (media != null) {
							try {
								Imports.fromFile(Medias.asFile(media),
										JeaseSession.getContainer(),
										JeaseSession.get(User.class));
							} catch (Exception e) {
								Modal.error(e.getMessage());
							} finally {
								refresh();
							}
						}
					}
				});
	}

	private Component newViewButton() {
		return new Button(I18N.get("View"), Images.InternetWebBrowser,
				new ActionListener() {
					public void actionPerformed(Event evt) {
						getRoot().appendChild(
								new WebBrowser(JeaseSession.getContainer()
										.getPath()));
					}
				});
	}
}
