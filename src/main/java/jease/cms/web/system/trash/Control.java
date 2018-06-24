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
package jease.cms.web.system.trash;

import jease.cms.domain.Content;
import jease.cms.service.Contents;
import jease.cms.service.Revisions;
import jfix.util.I18N;
import jfix.zk.Formbox;
import jfix.zk.Images;
import jfix.zk.ItemRenderer;
import jfix.zk.Modal;
import jfix.zk.Refreshable;
import jfix.zk.Selectfield;
import jfix.zk.Spinner;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;

public class Control extends Formbox implements Refreshable {

	Spinner trashDays = new Spinner(Revisions.getDays());
	Button emptyTrash = new Button(I18N.get("Empty_Trash"),
			Images.UserTrashFull);
	Selectfield type = new Selectfield();
	Spinner count = new Spinner(Revisions.getCount());
	Spinner days = new Spinner(Revisions.getDays());
	Button purge = new Button(I18N.get("Purge"), Images.EditClear);

	public Control() {
		setHflex(null);
		type.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				Content content = (Content) value;
				if (value != null) {
					return I18N.get(content.getType()) + " ("
							+ Revisions.getNumber(content.getClass()) + ")";
				} else {
					return "* (" + Revisions.getNumber(Content.class) + ")";
				}
			}
		});

		emptyTrash.addEventListener(Events.ON_CLICK,
				$event -> empyTrashPerformed());
		purge.addEventListener(Events.ON_CLICK, $event -> purgePerformed());

		add(I18N.get("Trash"));
		add(I18N.get("Days"), trashDays);
		add("", emptyTrash);

		add(I18N.get("Revision"));
		add(I18N.get("Type"), type);
		add(I18N.get("Count"), count);
		add(I18N.get("Days"), days);
		add("", purge);
	}

	private Class<Content> getSelectedType() {
		return type.getSelectedValue() != null ? (Class<Content>) type
				.getSelectedValue().getClass() : Content.class;
	}

	private void empyTrashPerformed() {
		Modal.confirm(I18N.get("Are_you_sure"), $event -> {
			Contents.emptyTrash(trashDays.intValue());
			Modal.info(I18N.get("Action_performed"));
			refresh();
		});
	}

	private void purgePerformed() {
		Modal.confirm(
				I18N.get("Are_you_sure"),
				$event -> {
					int result = Revisions.purge(getSelectedType(),
							count.intValue(), days.intValue());
					Modal.info(I18N.get("Revisions_purged") + ": " + result);
					refresh();
				});
	}

	public void refresh() {
		Object selectedValue = type.getSelectedValue();
		type.setValues(Contents.getAvailableTypes());
		type.setSelectedValue(selectedValue);
	}

}
