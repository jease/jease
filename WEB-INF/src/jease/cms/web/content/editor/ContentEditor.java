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
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.web.i18n.Strings;
import jfix.zk.Button;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.Textfield;

public abstract class ContentEditor<E extends Content> extends NodeEditor<E> {

	private Date lastNodeModification;
	private Button view = new Button(Strings.View, Images.Internet);
	protected Textfield title = new Textfield();

	protected void doInit() throws Exception {
		super.doInit();
		view.setTarget("_blank");
		getButtons().appendChild(view);
		add(Strings.Title, title);
		init();
	}

	protected void doLoad() throws Exception {
		super.doLoad();
		view.setVisible(Nodes.isRooted(getNode()));
		view.setHref(getNode().getPath());
		title.setText(getNode().getTitle());
		lastNodeModification = getNode().getLastModified();
		load();
	}

	protected void doSave() throws Exception {
		super.doSave();
		getNode().setTitle(title.getText());
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		save();
		Nodes.save(getNode());
	}

	protected void doValidate() throws Exception {
		super.doValidate();
		validate(title.isEmpty(), Strings.Title_is_required);
		validate(lastNodeModification != getNode().getLastModified(),
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

	public void delete() {
		getNode().setEditor(getSessionUser());
		getNode().setLastModified(new Date());
		Contents.delete(getNode());
	}

	protected User getSessionUser() {
		return JeaseSession.get(User.class);
	}

	protected Button getViewButton() {
		return view;
	}

}
