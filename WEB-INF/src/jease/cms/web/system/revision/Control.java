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
package jease.cms.web.system.revision;

import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.service.Contents;
import jease.cms.service.Revisions;
import jease.cms.web.i18n.Strings;
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
	Button purge = new Button(Strings.Purge, Images.EditClear);

	public Control() {
		setWidth("300px");

		type.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				return value != null ? ((Content) value).getType()
						: Content.class.getSimpleName();
			}
		});

		purge.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				purgePerformed();
			}
		});

		Formbox form = new Formbox();
		form.add(Strings.Revisions);
		form.add(Strings.Type, type);
		form.add(Strings.Count, count);
		form.add(Strings.Days, days);
		form.add("", purge);
		appendChild(form);
	}

	private Class<Content> getSelectedType() {
		return type.getSelectedValue() != null ? (Class<Content>) type
				.getSelectedValue().getClass() : Content.class;
	}

	private void purgePerformed() {
		Modal.confirm(Strings.Are_you_sure, new ActionListener() {
			public void actionPerformed(Event event) {
				int result = purge(getSelectedType(), count.intValue(),
						days.intValue());
				Modal.info(Strings.Revisions_purged + ": " + result);
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
