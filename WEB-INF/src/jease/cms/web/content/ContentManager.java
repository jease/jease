package jease.cms.web.content;

import java.io.File;

import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.User;
import jease.cms.service.Imports;
import jfix.zk.ActionListener;
import jfix.zk.Medias;
import jfix.zk.Modal;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;

/**
 * JeaseCMS with customized UploadListener for quick file import.
 */
public class ContentManager extends Jease {

	public ContentManager() {
		addUploadListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				Media media = ((UploadEvent) evt).getMedia();
				if (media != null) {
					importMedia(media);
				}
			}
		});
	}

	private void importMedia(Media media) {
		try {
			String mediaName = media.getName();
			File mediaFile = Medias.asFile(media);
			Imports.fromFile(mediaName, mediaFile, JeaseSession.getContainer(),
					JeaseSession.get(User.class));
			mediaFile.delete();
		} catch (Exception e) {
			Modal.error(e.getMessage());
		} finally {
			refresh();
		}
	}
}
