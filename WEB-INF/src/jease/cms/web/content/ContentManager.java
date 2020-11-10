package jease.cms.web.content;

import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.User;
import jease.cms.service.Imports;
import jfix.zk.ActionListener;
import jfix.zk.Div;
import jfix.zk.Fileupload;
import jfix.zk.Medias;
import jfix.zk.Modal;

import org.zkoss.util.media.Media;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.UploadEvent;

/**
 * JeaseCMS with additional fileupload for quick content import.
 */
public class ContentManager extends Jease {

	public ContentManager() {
		Fileupload fileupload = new Fileupload(new ActionListener() {
			public void actionPerformed(Event evt) {
				uploadPerformed(((UploadEvent) evt).getMedia());
			}
		});
		getAccessory().appendChild(new Div("text-align: right;", fileupload));
	}

	private void uploadPerformed(Media media) {
		if (media != null) {
			try {
				Imports.fromFile(Medias.asFile(media), JeaseSession
						.getContainer(), JeaseSession.get(User.class));
			} catch (Exception e) {
				Modal.error(e.getMessage());
			} finally {
				refresh();
			}
		}
	}

}
