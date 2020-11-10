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
package jease.cms.web.user;

import java.util.Comparator;

import jease.cmf.web.JeaseSession;
import jease.cms.domain.Content;
import jease.cms.domain.Role;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Users;
import jfix.db4o.Database;
import jfix.util.Arrays;
import jfix.util.I18N;
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
	Picklist roots = new Picklist(new Comparator<Content>() {
		public int compare(Content o1, Content o2) {
			return o1.getPath().compareTo(o2.getPath());
		}
	}, true);

	public Editor() {
		roots.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				return ((Content) value).getPath();
			}
		});
		if (!isAdministrationMode()) {
			hideButtons();
			getSaveButton().setVisible(true);
		}
	}

	public void init() {
		add(I18N.get("Name"), name);
		add(I18N.get("Login"), login);
		add(I18N.get("Password"), password);
		add(I18N.get("Password_Repeat"), passwordRepeat);
		add(I18N.get("Email"), email);
		if (isAdministrationMode()) {
			add(I18N.get("Role"), role);
			add(I18N.get("Roots"), roots);
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
			roots.setSelection(Contents.getContainer(), getObject().getRoots());
		}
	}

	public void save() {
		getObject().setName(name.getText());
		getObject().setLogin(login.getText());
		getObject().setPassword(password.getText());
		getObject().setEmail(email.getText());
		if (isAdministrationMode()) {
			getObject().setRole((Role) role.getSelectedValue());
			getObject().setRoots(
					Arrays.cast(roots.getSelected(), Content.class));
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
		validate(name.isEmpty(), I18N.get("Name_is_required"));
		validate(login.isEmpty(), I18N.get("Login_is_required"));
		validate(!password.getText().equals(passwordRepeat.getText()),
				I18N.get("Passwords_do_not_match"));
		validate(
				!Users.isIdentityUnique(getObject(), login.getValue(),
						email.getValue()), I18N.get("Identity_is_not_unique"));
	}

	private boolean isAdministrationMode() {
		return getSessionUser() != null && getSessionUser().isAdministrator();
	}

	private User getSessionUser() {
		return JeaseSession.get(User.class);
	}

}
