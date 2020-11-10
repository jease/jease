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
package jease.cms.web.system.redirect;

import java.util.Date;

import jease.cms.domain.Redirect;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectEditor;
import jfix.zk.Textfield;

public class Editor extends ObjectEditor<Redirect> {

	Textfield source = new Textfield();
	Textfield target = new Textfield();

	public Editor() {
	}

	public void init() {
		add(I18N.get("Source"), source);
		add(I18N.get("Target"), target);
	}

	public void load() {
		source.setText(getObject().getSource());
		target.setText(getObject().getTarget());
	}

	public void save() {
		getObject().setSource(source.getText());
		getObject().setTarget(target.getText());
		getObject().setTimestamp(new Date());
		Database.save(getObject());
	}

	public void delete() {
		Database.delete(getObject());
	}

	public void validate() {
		validate(source.isEmpty(), I18N.get("Source_is_required"));
		validate(target.isEmpty(), I18N.get("Target_is_required"));
	}

}
