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

import jease.cms.domain.User;
import jease.cms.web.content.ContentManager;
import jease.cms.web.i18n.Strings;
import jease.cms.web.system.Control;
import jfix.util.Validations;
import jfix.zk.Images;
import jfix.zk.Logout;
import jfix.zk.Tabbox;

public class Navigation extends Tabbox {

	public Navigation(User user) {
		if (Validations.isNotEmpty(user.getRoots())) {
			add(Strings.Content, ContentManager.class, Images.MailAttachment);
		}
		add(Strings.User, jease.cms.web.user.Table.class, Images.SystemUsers);
		if (user.isAdministrator()) {
			add(Strings.System, Control.class, Images.ApplicationsSystem);
		}
		add(Strings.Logout, Logout.class, Images.SystemLogOut);
	}

}
