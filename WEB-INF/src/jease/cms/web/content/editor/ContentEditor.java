/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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

import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cmf.web.node.browser.NodeViewer;
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Revisions;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.Selectfield;
import jfix.zk.Textfield;

import org.zkoss.zk.ui.event.Event;

/**
 * Base class for all content editors. All common fields for Content should be
 * added here.
 */
public abstract class ContentEditor<E extends Content> extends NodeEditor<E> {

	private Date lastNodeModification;
	private Button view = new Button(Strings.View, Images.Internet);
	protected Textfield title = new Textfield();
	protected Selectfield revisions = new Selectfield();

	public ContentEditor() {
		view.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				getRoot().appendChild(new NodeViewer(getNode()));
			}
		});
		getButtons().appendChild(view);

		revisions.setNullable(false);
		revisions.addSelectListener(new ActionListener() {
			public void actionPerformed(Event event) {
				peek(Revisions.checkout(getNode(), revisions.getSelectedIndex()));
				lastNodeModification = null;
			}
		});
	}

	protected void doInit() throws Exception {
		add(Strings.Revision, revisions);
		super.doInit();
		add(Strings.Title, title);
		init();
	}

	protected void doLoad() throws Exception {
		super.doLoad();
		view.setVisible(Nodes.isRooted(getNode()));
		lastNodeModification = getNode().getLastModified();
		if (title.getParent() != null) {
			title.setText(getNode().getTitle());
		}
		if (revisions.getParent() != null) {
			revisions.setValues(getNode().getRevisions());
		}
		load();
	}

	protected void doSave() throws Exception {
		super.doSave();
		if (title.getParent() != null) {
			getNode().setTitle(title.getText());
		}
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		save();
		persist();
	}

	protected void persist() {
		Revisions.checkin(getSessionUser().getLogin(), getNode());
		Nodes.save(getNode());
	}

	protected void doValidate() throws Exception {
		super.doValidate();
		if (title.getParent() != null) {
			validate(title.isEmpty(), Strings.Title_is_required);
		}
		validate(lastNodeModification != null
				&& lastNodeModification != getNode().getLastModified(),
				Strings.Content_is_stale);
		validate();
	}

	protected void doDelete() throws Exception {
		if (Contents.isDeletable(getNode())) {
			super.doDelete();
		} else {
			Modal.error(Strings.Content_is_not_deletable);
		}
	}

	public void doCopy() throws Exception {
		super.doCopy();
		if (revisions.getParent() != null) {
			revisions.setValues(new Object[] {});
		}
	}

	public void delete() {
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		Contents.delete(getNode());
	}

	protected User getSessionUser() {
		return JeaseSession.get(User.class);
	}

	public Button getViewButton() {
		return view;
	}

	public void hideButtons() {
		super.hideButtons();
		view.setVisible(false);
	}
}
