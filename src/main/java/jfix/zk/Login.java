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
package jfix.zk;

import jfix.util.I18N;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Separator;
import org.zkoss.zul.Textbox;

public abstract class Login extends Formbox {

	Textbox loginField = new Textbox();
	Textbox passwordField = new Textbox();

	public abstract boolean loginPerformed(String login, String password);

	public Login() {
		this(I18N.get("Username"), I18N.get("Password"));
	}

	public Login(String loginLabel, String passwordLabel) {
		loginField.setPlaceholder(loginLabel);
		passwordField.setPlaceholder(passwordLabel);
		passwordField.setType("password");
		Button submitButton = new Button(I18N.get("Login"), Images.SystemUsers);
		submitButton.setHflex("1");
		submitButton
				.addEventListener(Events.ON_CLICK, $event -> loginHandler());
		addEventListener(Events.ON_OK, $event -> loginHandler());

		Button resetButton = new Button(I18N.get("Reset"), Images.ViewRefresh);
		resetButton.setHflex("1");
		resetButton.addEventListener(Events.ON_CLICK, $event -> resetHandler());

		add(loginField);
		add(passwordField);
		add(new Div(submitButton, new Separator("vertical"), resetButton));
	}

	private void loginHandler() {
		String loginText = loginField.getText().trim();
		String passwordText = passwordField.getText().trim();
		if (!"".equals(loginText) && !"".equals(passwordText)) {
			if (!loginPerformed(loginText, passwordText)) {
				passwordField.setValue(null);
				passwordField.focus();
			}
		}
	}

	private void resetHandler() {
		loginField.setValue(null);
		passwordField.setValue(null);
		loginField.focus();
	}
}
