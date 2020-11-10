package jease.cms.web.content;

import jease.cmf.web.Jease;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.browser.NodeViewer;
import jease.cms.domain.User;
import jease.cms.service.Backups;
import jease.cms.service.Imports;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Div;
import jfix.zk.Filedownload;
import jfix.zk.Fileupload;
import jfix.zk.Images;
import jfix.zk.Medias;
import jfix.zk.Modal;

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
		Div container = new Div("text-align: right;");
		if (JeaseSession.get(User.class).isAdministrator()) {
			container.appendChild(newDumpButton());
			container.appendChild(newRestoreButton());
		}
		container.appendChild(newUploadButton());
		container.appendChild(newViewButton());
		getAccessory().appendChild(container);
	}

	private Component newDumpButton() {
		return new Button(Strings.Dump, Images.Dump, new ActionListener() {
			public void actionPerformed(Event evt) {
				Filedownload.save(Backups.dump(JeaseSession.getContainer()));
			}
		});
	}

	private Component newRestoreButton() {
		return new Fileupload(Strings.Restore, Images.Restore,
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
		return new Fileupload(Strings.Upload, Images.Home,
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
		return new Button(Strings.View, Images.Internet, new ActionListener() {
			public void actionPerformed(Event evt) {
				getRoot().appendChild(
						new NodeViewer(JeaseSession.getContainer()));
			}
		});
	}

}
