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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Components;
import org.zkoss.zul.Div;
import org.zkoss.zul.Window;

public abstract class LoginWindow extends Div {

	Login login = new Login() {
		public boolean loginPerformed(String user, String password) {
			doLogin(user, password);
			if (login.getParent().getParent() != null) {
				Modal.error(I18N.get("Login_is_not_valid"));
				return false;
			} else {
				return true;
			}
		}
	};

	public abstract void doLogin(String user, String password);

	public void show(Component comp) {
		Components.replace(login.getParent(), comp);
	}

	public LoginWindow() {
		Window window = new Window(getTitle(), "normal", false);
		window.setPosition("center");
		window.setWidth("300px");
		window.doOverlapped();
		window.appendChild(login);
		appendChild(window);
	}

	public String getTitle() {
		return I18N.get("Login");
	}

}