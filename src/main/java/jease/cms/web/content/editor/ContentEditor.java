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

import java.util.Date;

import jease.Names;
import jease.Registry;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cms.domain.Content;
import jease.cms.domain.Factory;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Properties;
import jease.cms.service.Revisions;
import jease.cms.service.Sequences;
import jease.cms.web.content.editor.property.PropertyManager;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.Selectfield;
import jfix.zk.Sessions;
import jfix.zk.WebBrowser;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

/**
 * Base class for all content editors. All common fields for Content should be
 * added here.
 */
public abstract class ContentEditor<E extends Content> extends NodeEditor<E> {

	protected Date lastNodeModification;
	protected Textbox title = new Textbox();
	protected Selectfield revisionSelection = new Selectfield();
	protected PropertyManager propertyManager = new PropertyManager();
	protected Button editProperties = new Button(I18N.get("Properties"),
			Images.DocumentProperties);
	protected Button viewContent = new Button(I18N.get("View"),
			Images.InternetWebBrowser);
	protected boolean closeCheckEnabled;

	public ContentEditor() {
		if (getSessionUser().isAdministrator()) {
			editProperties.addEventListener(Events.ON_CLICK,
					event -> propertyManager.toogleEdit());
			getButtons().appendChild(editProperties);
		}

		if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
			viewContent.addEventListener(Events.ON_CLICK,
					event -> viewContent());
			getButtons().appendChild(viewContent);
		}

		revisionSelection.addEventListener(
				Events.ON_SELECT,
				event -> {
					peek(Revisions.checkout(getNode(),
							revisionSelection.getSelectedIndex()));
					viewContent.setVisible(true);
					lastNodeModification = null;
				});

		addEventListener(Events.ON_CLOSE, event -> closePerformed(event));
		closeCheckEnabled = true;
	}

	public void refresh() {
		super.refresh();
		notifyAboutMaintenance();
	}

	protected void notifyAboutMaintenance() {
		String message = Registry.getParameter(Names.JEASE_CMS_MAINTENANCE);
		if (StringUtils.isNotBlank(message)
				&& !getSessionUser().isAdministrator()) {
			Modal.info(message, event -> Sessions.invalidate());
		}
	}

	protected void doInit() throws Exception {
		if (Revisions.isConfigured()) {
			add(I18N.get("Revision"), revisionSelection);
		}
		super.doInit();
		add(I18N.get("Title"), title);
		init();
		add(propertyManager);
	}

	protected void doLoad() throws Exception {
		super.doLoad();
		viewContent.setVisible(Nodes.isRooted(getNode()));
		lastNodeModification = getNode().getLastModified();
		if (title.getParent() != null) {
			title.setText(getNode().getTitle());
		}
		if (revisionSelection.getParent() != null) {
			revisionSelection.setValues(getNode().getRevisions());
		}
		if (propertyManager.getParent() != null) {
			Factory factory = Properties.getFactory(
					(Content) JeaseSession.getContainer(), getNode());
			if (factory != null && factory != JeaseSession.getContainer()) {
				if (StringUtils.isEmpty(id.getValue())
						&& StringUtils.isNotBlank(factory.getSequence())) {
					id.setText(String.valueOf(Sequences.getNext(factory
							.getSequence())));
				}
				propertyManager.setProperties(factory.getProperties(getNode()));
			} else {
				propertyManager.setProperties(getNode().getProperties());
			}
		}
		load();
	}

	protected void saveEditorToObject() throws Exception {
		super.doSave();
		if (getNode().getCreationDate() == null) {
			getNode().setCreationDate(new Date());
		}
		saveLastModification();
		if (title.getParent() != null) {
			getNode().setTitle(title.getText());
		}
		if (propertyManager.getParent() != null) {
			getNode().setProperties(propertyManager.getProperties());
		}
		save();
	}

	protected void doSave() throws Exception {
		saveEditorToObject();
		persist();
	}

	protected void saveLastModification() {
		User lastEditor = getNode().getEditor();
		User currentUser = getSessionUser();
		if (lastEditor == null || lastEditor.isAdministrator()
				|| !currentUser.isAdministrator()) {
			getNode().setEditor(currentUser);
			getNode().setLastModified(new Date());
		}
	}

	protected void persist() {
		if (Revisions.isConfigured()) {
			Revisions.checkin(getSessionUser().getLogin(), getNode());
		}
		Nodes.save(Contents.customize(getNode()));
	}

	protected void doValidate() throws Exception {
		if (StringUtils.isEmpty(id.getValue()) && id.isVisible()
				&& !id.isDisabled()) {
			id.setValue(title.getValue());
		}
		super.doValidate();
		if (!isFactoryMode()) {
			if (title.getParent() != null) {
				validate(StringUtils.isEmpty(title.getValue()),
						I18N.get("Title_is_required"));
			}
			validate(lastNodeModification != null
					&& lastNodeModification != getNode().getLastModified(),
					I18N.get("Content_is_stale"));
			validate();
		}
	}

	public void doCopy() throws Exception {
		super.doCopy();
		if (revisionSelection.getParent() != null) {
			revisionSelection.setValues(new Object[] {});
		}
		viewContent.setVisible(false);
	}

	public void delete() {
		closeCheckEnabled = false;
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		Contents.delete(getNode());
	}

	protected User getSessionUser() {
		return JeaseSession.get(User.class);
	}

	public void hideButtons() {
		super.hideButtons();
		viewContent.setVisible(false);
		editProperties.setVisible(false);
	}

	protected boolean isFactoryMode() {
		return JeaseSession.getContainer() instanceof Factory;
	}

	protected int getDesktopHeight() {
		return (Integer) JeaseSession.get(Names.JEASE_CMS_HEIGHT);
	}

	protected int getDesktopWidth() {
		return (Integer) JeaseSession.get(Names.JEASE_CMS_WIDTH);
	}

	protected void closePerformed(final Event event) {
		if (closeCheckEnabled) {
			final E currentNode = getNode();
			String currentFulltext = getNode().getFulltext().toString();
			try {
				copyObject(false);
				saveEditorToObject();
				getNode().setParent(null);
				String newFulltext = getNode().getFulltext().toString();
				if (!StringUtils.equals(currentFulltext, newFulltext)) {
					event.stopPropagation();
					Modal.confirm(I18N.get("Are_you_sure"), evt -> {
						closeCheckEnabled = false;
						refresh();
						fireClose();
					});
				}
			} catch (Exception e) {
				Modal.exception(e);
				return;
			} finally {
				setObject(currentNode);
			}
		}
	}

	protected void viewContent() {
		final E currentNode = getNode();
		try {
			copyObject(false);
			saveEditorToObject();
			getNode().setParent(null);
			Sessions.set(currentNode.getPath(), getNode());
		} catch (Exception e) {
			Modal.exception(e);
			return;
		} finally {
			setObject(currentNode);
		}
		WebBrowser browser = new WebBrowser(getNode().getPath());
		browser.addEventListener(Events.ON_CLOSE,
				event -> Sessions.remove(currentNode.getPath()));
		getRoot().appendChild(browser);
	}

}
