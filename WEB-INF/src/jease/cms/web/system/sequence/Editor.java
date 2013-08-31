/*
    Copyright (C) 2013 maik.jablonski@jease.org

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
package jease.cms.web.system.sequence;

import jease.cms.domain.Sequence;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectEditor;
import jfix.zk.Spinner;
import jfix.zk.Textfield;

public class Editor extends ObjectEditor<Sequence> {

	Textfield name = new Textfield();
	Spinner value = new Spinner();

	public Editor() {
	}

	public void init() {
		add(I18N.get("Name"), name);
		add(I18N.get("Value"), value);
	}

	public void load() {
		name.setText(getObject().getName());
		value.setValue(getObject().getValue());
	}

	public void save() {
		getObject().setName(name.getText());
		getObject().setValue(value.intValue());
		Database.save(getObject());
	}

	public void delete() {
		Database.delete(getObject());
	}

	public void validate() {
		validate(name.isEmpty(), I18N.get("Name_is_required"));
	}

}
