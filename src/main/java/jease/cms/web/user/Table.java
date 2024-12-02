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
package jease.cms.web.user;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zul.Button;

import jease.cms.domain.User;
import jease.cms.service.Backups;
import jfix.util.I18N;
import jfix.zk.Filedownload;
import jfix.zk.Fileupload;
import jfix.zk.Images;
import jfix.zk.Medias;
import jfix.zk.Modal;
import jfix.zk.ObjectTable;

public class Table extends ObjectTable<User> {

    private static final Logger LOGGER = LoggerFactory.getLogger(Table.class);

    public Table() {
        init(new TableModel(), new Editor());

        Button dump = new Button(I18N.get("Dump"), Images.DriveCdrom);
        dump.setStyle("margin-left: 20px");
        dump.addEventListener(Events.ON_CLICK, event -> Filedownload.save(Backups.dumpUsers()));
        getLeftbox().appendChild(dump);

        Fileupload restore = new Fileupload(I18N.get("Restore"), Images.MediaCdrom);
        restore.addEventListener(Events.ON_UPLOAD, event -> {
            Media media = ((UploadEvent) event).getMedia();
            if (media != null) {
                boolean restored = false;
                try {
                    File backupFile = Medias.asFile(media);
                    backupFile.deleteOnExit();
                    restored = Backups.restoreUsers(backupFile);
                } catch (Exception e) {
                    LOGGER.error("Restore users failed", e);
                    Modal.error(e.getMessage());
                } finally {
                    if (restored) refresh();
                }
            }
        });
        getLeftbox().appendChild(restore);
    }
}
