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
package jease.cms.web.content.editor;

import jease.cms.domain.Access;
import jfix.util.I18N;
import jfix.zk.Datetimefield;
import jfix.zk.Passwordfield;
import jfix.zk.Textfield;

public class AccessEditor extends ContentEditor<Access> {

	Textfield login = new Textfield();
	Passwordfield password = new Passwordfield();
	Passwordfield passwordRepeat = new Passwordfield();
	Datetimefield start = new Datetimefield();
	Datetimefield stop = new Datetimefield();

	public void init() {
		add(I18N.get("Login"), login);
		add(I18N.get("Password"), password);
		add(I18N.get("Password_Repeat"), passwordRepeat);
		add(I18N.get("Start"), start);
		add(I18N.get("Stop"), stop);
	}

	public void load() {
		login.setText(getNode().getLogin());
		password.setText(getNode().getPassword());
		passwordRepeat.setText(getNode().getPassword());
		start.setValue(getNode().getStart());
		stop.setValue(getNode().getStop());
	}

	public void save() {
		getNode().setLogin(login.getText());
		getNode().setPassword(password.getText());
		getNode().setStart(start.getValue());
		getNode().setStop(stop.getValue());
	}

	public void validate() {
		validate(login.isEmpty(), I18N.get("Login_is_required"));
		validate(password.isEmpty(), I18N.get("Password_is_required"));
		validate(!password.getText().equals(passwordRepeat.getText()),
				I18N.get("Passwords_do_not_match"));
	}
}
