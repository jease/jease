/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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

import java.util.*;

import jease.cmf.service.*;
import jease.cmf.web.*;
import jease.cmf.web.node.*;
import jease.cms.domain.*;
import jease.cms.service.*;
import jease.cms.web.i18n.*;
import jfix.zk.*;

public abstract class ContentEditor<E extends Content> extends NodeEditor<E> {

	protected Textfield title = new Textfield();

	protected void doInit() throws Exception {
		super.doInit();
		add(Strings.Title, title);
		init();
	}

	protected void doLoad() throws Exception {
		super.doLoad();
		title.setText(getNode().getTitle());
		load();
	}

	protected void doSave() throws Exception {
		super.doSave();
		getNode().setTitle(title.getText());
		getNode().setLastModified(new Date());
		getNode().setEditor(JeaseSession.get(User.class));
		save();
		Nodes.save(getNode());
	}

	protected void doValidate() throws Exception {
		super.doValidate();
		validate(title.isEmpty(), Strings.Title_is_required);
		validate();
	}

	protected void doDelete() throws Exception {
		if (Contents.isDeletable(getObject())) {
			super.doDelete();
		} else {
			Modal.error(Strings.Content_is_not_deletable);
		}
	}
}
