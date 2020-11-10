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

import jease.cms.domain.Content;
import jease.cms.domain.Reference;
import jease.cms.service.Contents;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Selectbutton;

import org.zkoss.zk.ui.event.Event;

public class ReferenceEditor extends ContentEditor<Reference> {

	Selectbutton content = new Selectbutton();

	public ReferenceEditor() {
		content.addSelectListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				contentSelected((Content) content.getSelectedValue());
			}
		});
	}

	public void init() {
		add(Strings.Content, content);
	}

	public void load() {
		content.setModel(Contents.getDescendants(getSessionUser().getRoots()),
				getNode().getContent());
	}

	public void save() {
		getNode().setContent((Content) content.getSelectedValue());
	}

	public void validate() {
		validate(content.isEmpty(), Strings.Content_is_required);
	}

	private void contentSelected(Content content) {
		if (content != null) {
			if (id.isEmpty()) {
				id.setText(content.getId());
			}
			if (title.isEmpty()) {
				title.setText(content.getTitle());
			}
		}
	}
}
