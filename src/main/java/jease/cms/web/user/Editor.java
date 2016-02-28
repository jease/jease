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
package jease.cms.web.user;

import jease.cmf.web.JeaseSession;
import jease.cms.domain.Content;
import jease.cms.domain.Role;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Passwords;
import jease.cms.service.Users;
import jfix.db4o.Database;
import jfix.util.Crypts;
import jfix.util.I18N;
import jfix.util.Natural;
import jfix.zk.ItemRenderer;
import jfix.zk.ObjectEditor;
import jfix.zk.Picklist;
import jfix.zk.Selectfield;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Textbox;

import java.util.Arrays;

public class Editor extends ObjectEditor<User> {

	Textbox name = new Textbox();
	Textbox username = new Textbox();
	Textbox password = new Textbox();
	Textbox passwordRepeat = new Textbox();
	Textbox email = new Textbox();
	Selectfield role = new Selectfield();
	Picklist roots = new Picklist((o1, o2) -> {return ((Content) o1).getPath().compareTo(((Content) o2).getPath());}, true);
	Checkbox disabled = new Checkbox();
	String passwordExample = I18N.get("Example") + ": " + Crypts.generatePassword(8);

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
		password.setType("password");
		passwordRepeat.setType("password");
	}

	public void init() {
		add(I18N.get("Name"), name);
		add(I18N.get("Username"), username);
		add(I18N.get("Password"), password, passwordExample);
		add(I18N.get("Password_Repeat"), passwordRepeat, passwordExample);
		add(I18N.get("Email"), email);
		if (isAdministrationMode()) {
			add(I18N.get("Role"), role);
			add(I18N.get("Roots"), roots);
			add(I18N.get("Disabled"), disabled);
		}
	}

	public void load() {
		name.setText(getObject().getName());
		username.setText(getObject().getLogin());
		password.setText(getObject().getPassword());
		passwordRepeat.setText(getObject().getPassword());
		email.setText(getObject().getEmail());
		if (isAdministrationMode()) {
			role.setSelection(Natural.sort(Database.query(Role.class)), getObject().getRole());
			roots.setSelection(Contents.getContainer(), getObject().getRoots());
			disabled.setChecked(getObject().isDisabled());
			disabled.setDisabled(getSessionUser() == getObject());
			getDeleteButton().setVisible(getSessionUser() != getObject());
		}
	}

	public void save() {
		getObject().setName(name.getText());
		getObject().setLogin(username.getText());
		getObject().setPassword(password.getText());
		getObject().setEmail(email.getText());
		if (isAdministrationMode()) {
			getObject().setRole((Role) role.getSelectedValue());
			getObject().setRoots(Arrays.asList(roots.getSelected()).toArray(new Content[roots.getSelected().length]));
			getObject().setDisabled(disabled.isChecked());
		}
		Database.save(getObject());
	}

	public void delete() {
		Users.replace(getObject(), getSessionUser());
		Database.delete(getObject());
	}

	public void validate() {
		validate(StringUtils.isEmpty(name.getValue()), I18N.get("Name_is_required"));
		validate(StringUtils.isEmpty(username.getValue()), I18N.get("Login_is_required"));
		validate(StringUtils.isEmpty(password.getValue()), I18N.get("Password_is_required"));
		validate(!password.getText().equals(passwordRepeat.getText()), I18N.get("Passwords_do_not_match"));
		validate(!(StringUtils.isBlank(password.getText())
				|| StringUtils.equals(password.getText(), getObject().getPassword())
				|| Passwords.isValid(password.getText())), I18N.get("Password_is_not_reliable") + "\n" + passwordExample);
		validate(!Users.isIdentityUnique(getObject(), username.getValue(),
				email.getValue()), I18N.get("Identity_is_not_unique"));
	}

	protected boolean isAdministrationMode() {
		return getSessionUser() != null && getSessionUser().isAdministrator();
	}

	protected User getSessionUser() {
		return JeaseSession.get(User.class);
	}
}
