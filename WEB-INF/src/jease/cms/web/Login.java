/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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

import jease.cmf.service.*;
import jease.cmf.web.*;
import jease.cms.domain.*;
import jease.cms.service.*;
import jease.cms.web.content.*;
import jease.cms.web.i18n.*;
import jfix.util.*;
import jfix.zk.*;

public class Login extends LoginWindow {

	public String getTitle() {
		return Nodes.getRoot() != null ? Nodes.getRoot().getTitle() : super
				.getTitle();
	}

	public void doLogin(String login, String password) {
		User user = Users.queryByLogin(login, password);
		if (user != null) {
			if (Validations.isNotEmpty(user.getRoots())) {
				initJeaseSession(user);
			}
			showNavigation(user);
		}
	}

	private void initJeaseSession(User user) {
		JeaseSession.set(user);
		JeaseSession.setRoots(user.getRoots());
		JeaseSession.setContainer(user.getRoots()[0]);
		JeaseSession.setConfig(new Configuration());
	}

	private void showNavigation(User user) {
		Tabbox tabs = new Tabbox();
		if (Validations.isNotEmpty(user.getRoots())) {
			tabs.add(Strings.Content, ContentManager.class);
		}
		if (user.isAdministrator()) {
			tabs.add(Strings.User, jease.cms.web.user.Table.class);
		}
		tabs.add(Strings.Logout, jfix.zk.Logout.class);
		show(tabs);
	}
}
