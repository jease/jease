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
package jease.cms.web.system.parameter;

import jease.cms.domain.Parameter;
import jfix.db4o.Database;
import jfix.functor.Predicate;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Checkbox;
import jfix.zk.Div;
import jfix.zk.ObjectEditor;
import jfix.zk.Textarea;
import jfix.zk.Textfield;

import org.zkoss.zk.ui.event.Event;

public class Editor extends ObjectEditor<Parameter> {

	private static int MULTILINES = 20;

	Textfield key = new Textfield();
	Textarea value = new Textarea();
	Checkbox multiline = new Checkbox(I18N.get("Multiline"));

	public Editor() {
		multiline.addCheckListener(new ActionListener() {
			public void actionPerformed(Event event) {
				value.setRows(multiline.isChecked() ? 20 : 1);
			}
		});
	}

	public void init() {
		add(I18N.get("Key"), key);
		add(I18N.get("Value"), value);
		add("", new Div("text-align: right", multiline));
	}

	public void load() {
		key.setText(getObject().getKey());
		value.setText(getObject().getValue());
		if (value.getText().contains("\n")) {
			multiline.setChecked(true);
			multiline.setVisible(false);
			value.setRows(MULTILINES);
		} else {
			multiline.setChecked(false);
			multiline.setVisible(true);
			value.setRows(1);
		}
	}

	public void save() {
		getObject().setKey(key.getText());
		getObject().setValue(value.getText());
		Database.save(getObject());
	}

	public void delete() {
		Database.delete(getObject());
	}

	public void validate() {
		validate(key.isEmpty(), I18N.get("Key_is_required"));
		validate(!Database.isUnique(getObject(), new Predicate<Parameter>() {
			public boolean test(Parameter parameter) {
				return parameter.getKey().equals(key.getText());
			}
		}), I18N.get("Key_must_be_unique"));
	}
}
