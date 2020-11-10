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
package jease.cms.web.user;

import java.util.Comparator;

import jease.cmf.web.JeaseSession;
import jease.cms.domain.Folder;
import jease.cms.domain.Role;
import jease.cms.domain.User;
import jease.cms.service.Users;
import jease.cms.web.i18n.Strings;
import jfix.db4o.Database;
import jfix.util.Arrays;
import jfix.util.Natural;
import jfix.zk.ItemRenderer;
import jfix.zk.ObjectEditor;
import jfix.zk.Passwordfield;
import jfix.zk.Picklist;
import jfix.zk.Selectfield;
import jfix.zk.Sessions;
import jfix.zk.Textfield;

public class Editor extends ObjectEditor<User> {

	Textfield name = new Textfield();
	Textfield login = new Textfield();
	Passwordfield password = new Passwordfield();
	Passwordfield passwordRepeat = new Passwordfield();
	Textfield email = new Textfield();
	Selectfield role = new Selectfield();
	Picklist roots = new Picklist(new Comparator<Folder>() {
		public int compare(Folder o1, Folder o2) {
			return o1.getPath().compareTo(o2.getPath());
		}
	}, true);

	public Editor() {
		roots.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				return ((Folder) value).getPath();
			}
		});
		if (!isAdministrationMode()) {
			hideButtons();
			getSaveButton().setVisible(true);
		}
	}

	public void init() {
		add(Strings.Name, name);
		add(Strings.Login, login);
		add(Strings.Password, password);
		add(Strings.Password_Repeat, passwordRepeat);
		add(Strings.Email, email);
		if (isAdministrationMode()) {
			add(Strings.Role, role);
			add(Strings.Roots, roots);
		}
	}

	public void load() {
		name.setText(getObject().getName());
		login.setText(getObject().getLogin());
		password.setText(getObject().getPassword());
		passwordRepeat.setText(getObject().getPassword());
		email.setText(getObject().getEmail());
		if (isAdministrationMode()) {
			role.setSelection(Natural.sort(Database.query(Role.class)),
					getObject().getRole());
			roots.setSelection(Database.query(Folder.class), getObject()
					.getRoots());
		}
	}

	public void save() {
		getObject().setName(name.getText());
		getObject().setLogin(login.getText());
		getObject().setPassword(password.getText());
		getObject().setEmail(email.getText());
		if (isAdministrationMode()) {
			getObject().setRole((Role) role.getSelectedValue());
			getObject()
					.setRoots(Arrays.cast(roots.getSelected(), Folder.class));
		}
		Database.save(getObject());
	}

	public void delete() {
		if (getObject() != getSessionUser()) {
			Users.replace(getObject(), getSessionUser());
			Database.delete(getObject());
		} else {
			Users.replace(getObject(), null);
			Database.delete(getObject());
			Sessions.invalidate();
		}
	}

	public void validate() {
		validate(name.isEmpty(), Strings.Name_is_required);
		validate(login.isEmpty(), Strings.Login_is_required);
		validate(!password.getText().equals(passwordRepeat.getText()),
				Strings.Passwords_do_not_match);
		validate(!Users.isIdentityUnique(getObject(), login.getValue(),
				email.getValue()), Strings.Identity_is_not_unique);
	}

	private boolean isAdministrationMode() {
		return getSessionUser() != null && getSessionUser().isAdministrator();
	}

	private User getSessionUser() {
		return JeaseSession.get(User.class);
	}

}
