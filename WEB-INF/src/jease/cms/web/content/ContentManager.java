package jease.cms.web.content;

import java.io.File;

import jease.cmf.web.*;
import jease.cms.domain.*;
import jease.cms.service.*;
import jfix.zk.*;

import org.zkoss.util.media.*;
import org.zkoss.zk.ui.event.*;

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
