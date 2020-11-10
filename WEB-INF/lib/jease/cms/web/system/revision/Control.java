/*
    Copyright (C) 2011 maik.jablonski@jease.org

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
package jease.cms.web.system.revision;

import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.service.Contents;
import jease.cms.service.Revisions;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Div;
import jfix.zk.Formbox;
import jfix.zk.Images;
import jfix.zk.ItemRenderer;
import jfix.zk.Modal;
import jfix.zk.Selectfield;
import jfix.zk.Spinner;

import org.zkoss.zk.ui.event.Event;

public class Control extends Div {

	Selectfield type = new Selectfield(Contents.getAvailableTypes());
	Spinner count = new Spinner(Revisions.getCount());
	Spinner days = new Spinner(Revisions.getDays());
	Button purge = new Button(I18N.get("Purge"), Images.EditClear);

	public Control() {
		setWidth("300px");

		type.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				return value != null ? ((Content) value).getType() : "*";
			}
		});

		purge.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				purgePerformed();
			}
		});

		Formbox form = new Formbox();
		form.add(I18N.get("Revision"));
		form.add(I18N.get("Type"), type);
		form.add(I18N.get("Count"), count);
		form.add(I18N.get("Days"), days);
		form.add("", purge);
		appendChild(form);
	}

	private Class<Content> getSelectedType() {
		return type.getSelectedValue() != null ? (Class<Content>) type
				.getSelectedValue().getClass() : Content.class;
	}

	private void purgePerformed() {
		Modal.confirm(I18N.get("Are_you_sure"), new ActionListener() {
			public void actionPerformed(Event event) {
				int result = purge(getSelectedType(), count.intValue(),
						days.intValue());
				Modal.info(I18N.get("Revisions_purged") + ": " + result);
			}
		});
	}

	private static int purge(Class<Content> clazz, int count, int days) {
		int result = 0;
		for (Content content : Nodes.getRoot().getDescendants(clazz)) {
			result += Revisions.purge(content, count, days);
			Nodes.save(content);
		}
		return result;
	}

}
