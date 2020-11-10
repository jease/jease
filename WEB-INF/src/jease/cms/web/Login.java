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
package jease.cms.web;

import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jease.cms.service.Users;
import jease.cms.web.content.Configuration;
import jease.cms.web.content.ContentManager;
import jease.cms.web.i18n.Strings;
import jfix.util.Validations;
import jfix.zk.LoginWindow;
import jfix.zk.Tabbox;

/**
 * Login into JeaseCMS: init user session and display tab-navigation.
 * 
 * If a user is already stored within the session, we don't reinforce a login,
 * but recreate the desktop from scratch. This avoids showing the login page
 * when the users refreshes the browser.
 */
public class Login extends LoginWindow {

	public Login() {
		super();
		User user = JeaseSession.get(User.class);
		if (user != null) {
			initSession(user);
			showNavigation(user);
		}
	}

	public String getTitle() {
		return Nodes.getRoot() != null ? ((Content) Nodes.getRoot()).getTitle()
				: super.getTitle();
	}

	public void doLogin(String login, String password) {
		User user = Users.queryByLogin(login, password);
		if (user != null) {
			initSession(user);
			showNavigation(user);
		}
	}

	private void initSession(User user) {
		JeaseSession.set(user);
		if (Validations.isNotEmpty(user.getRoots())) {
			JeaseSession.setRoots(user.getRoots());
			if (JeaseSession.getContainer() == null
					|| !JeaseSession.getContainer().isDescendant(
							user.getRoots())) {
				JeaseSession.setContainer(user.getRoots()[0]);
			}
			JeaseSession.setConfig(new Configuration());
		}
	}

	private void showNavigation(User user) {
		Tabbox tabs = new Tabbox();
		if (Validations.isNotEmpty(user.getRoots())) {
			tabs.add(Strings.Content, ContentManager.class);
		}
		tabs.add(Strings.User, jease.cms.web.user.Table.class);
		tabs.add(Strings.Logout, jfix.zk.Logout.class);
		show(tabs);
	}
}
