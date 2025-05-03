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

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;

import jease.Names;
import jease.Registry;
import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.User;
import jease.cms.service.Backups;
import jease.cms.service.Imports;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Filedownload;
import jfix.zk.Fileupload;
import jfix.zk.Images;
import jfix.zk.Medias;
import jfix.zk.Modal;
import jfix.zk.WebBrowser;

/**
 * JeaseCMS with additional content dump/restore and fileupload for quick
 * content import. Dump/Restore is only available for administrators to avoid
 * users importing scripts via restore-operation.
 */
public class ContentManager extends Jease {

    private Button view;
    private Button dump;
    private Fileupload importBtn;
    private Checkbox idWithoutExtensionCB;
    private Fileupload restore;
    private Fileupload upload;

    public ContentManager() {
        getFlatTable().getLeftbox().getChildren().clear();
        Component container = getTreeTable().getRightbox();
        container.getChildren().clear();
        initUploadButton();
        initImportButton();
        initIdWithoutExtensionCheckbox();

        container.appendChild(new Div("margin-top: 8px", idWithoutExtensionCB));
        container.appendChild(upload);
        container.appendChild(importBtn);
        container.appendChild(new Label("\u00A0\u00A0\u00A0\u00A0"));
        if (JeaseSession.get(User.class).isAdministrator()) {
            initDumpButton();
            initRestoreButton();
            container.appendChild(dump);
            container.appendChild(restore);
        }
        if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
            initViewButton();
            container.appendChild(view);
        }
    }

    private void initDumpButton() {
        dump = new Button(I18N.get("Dump"), Images.DriveCdrom);
        dump.addEventListener(Events.ON_CLICK, event ->
            Filedownload.save(Backups.dump(JeaseSession.getContainer()))
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

    private void initImportButton() {
        importBtn = new Fileupload(I18N.get("Import"), Images.DocumentSave, false);
        importBtn.addEventListener(Events.ON_UPLOAD, event -> {
            Media media = ((UploadEvent) event).getMedia();
            if (media != null) {
                Modal.confirm(I18N.get("Confirm_replace"), event1 -> {
                    try {
                        performUpload(media, true, idWithoutExtensionCB.isChecked());
                        Modal.info(I18N.get("Action_performed"));
                    } finally {
                        refresh();
                    }
                });
            }
        });
        setUploadLimit();
    }

    private void initIdWithoutExtensionCheckbox() {
        idWithoutExtensionCB = new Checkbox(I18N.get("Id_without_ext"));
        idWithoutExtensionCB.setTooltiptext(I18N.get("Id_without_ext_hint"));
        idWithoutExtensionCB.setChecked(true);
    }

    private void initUploadButton() {
        upload = new Fileupload(I18N.get("Upload"), Images.UserHome, true/*multiple*/);
        upload.addEventListener(Events.ON_UPLOAD, event -> {
            Media[] medias = ((UploadEvent) event).getMedias();
            if (medias != null) {
                try {
                    for (int i = 0; i < medias.length; i++) {
                        Media media = medias[i];
                        performUpload(media, false, idWithoutExtensionCB.isChecked());
                    }
                } finally {
                    refresh();
                }
            }
        });
        //
        setUploadLimit();
    }

    private static void performUpload(Media media, boolean replaceExisting, boolean idWithoutExtension) {
        try {
            File inputFile = Medias.asFile(media);
            inputFile.deleteOnExit();
            Imports.fromFile(inputFile,
                    JeaseSession.getContainer(), JeaseSession.get(User.class), replaceExisting, idWithoutExtension);
        } catch (Exception e) {
            Modal.error(e.getMessage());
        }
    }

    private void setUploadLimit() {
        final String limit = Registry.getParameter(Names.JEASE_UPLOAD_LIMIT);
        if (upload != null) upload.setUploadLimit(limit);
    }

    private void initViewButton() {
        view = new Button(I18N.get("View"), Images.InternetWebBrowser);
        view.addEventListener(
                Events.ON_CLICK, event -> {
                    getRoot().appendChild(new WebBrowser(JeaseSession.getContainer().getPath()));
                });
    }

    @Override
    public void refresh() {
        super.refresh();
        setUploadLimit();
    }
}
