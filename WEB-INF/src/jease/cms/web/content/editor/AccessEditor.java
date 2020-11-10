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

import jease.cms.domain.Access;
import jease.cms.web.i18n.Strings;
import jfix.zk.Passwordfield;
import jfix.zk.Textfield;

public class AccessEditor extends ContentEditor<Access> {

	Textfield login = new Textfield();
	Passwordfield password = new Passwordfield();
	Passwordfield passwordRepeat = new Passwordfield();

	public AccessEditor() {
	}

	public void init() {
		add(Strings.Login, login);
		add(Strings.Password, password);
		add(Strings.Password_Repeat, passwordRepeat);
	}

	public void load() {
		login.setText(getNode().getLogin());
		password.setText(getObject().getPassword());
		passwordRepeat.setText(getObject().getPassword());
	}

	public void save() {
		getNode().setLogin(login.getText());
		getNode().setPassword(password.getText());
	}

	public void validate() {
		validate(login.isEmpty(), Strings.Login_is_required);
		validate(password.isEmpty(), Strings.Password_is_required);
		validate(!password.getText().equals(passwordRepeat.getText()),
				Strings.Passwords_do_not_match);
	}
}
