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
package jease.cms.web;

import jease.Names;
import jease.Registry;
import jease.cms.domain.User;
import jease.cms.web.content.ContentManager;
import jfix.util.I18N;
import jfix.util.Validations;
import jfix.zk.Images;
import jfix.zk.Logout;
import jfix.zk.Tabbox;

public class Navigation extends Tabbox {

	public Navigation(User user) {
		if (Validations.isNotEmpty(user.getRoots())) {
			add(I18N.get("Content"), ContentManager.class,
					Images.MailAttachment);
		}
		if (user.isAdministrator()
				|| Registry.getParameter(Names.JEASE_CMS_AUTHENTICATOR) == null) {
			add(I18N.get("User"), jease.cms.web.user.Table.class,
					Images.SystemUsers);
		}
		if (user.isAdministrator()) {
			add(I18N.get("System"), System.class, Images.ApplicationsSystem);
		}
		add(I18N.get("Logout"), Logout.class, Images.SystemLogOut);
	}

	public static class System extends Tabbox {
		public System() {
			add(I18N.get("Parameter"),
					jease.cms.web.system.parameter.Table.class,
					Images.PreferencesSystem);
			add(I18N.get("Link_Check"),
					jease.cms.web.system.linkcheck.Table.class,
					Images.NetworkTransmit);
			add(I18N.get("Redirect"),
					jease.cms.web.system.redirect.Table.class,
					Images.EmblemSymbolicLink);
			add(I18N.get("Revision"),
					jease.cms.web.system.revision.Control.class,
					Images.EditUndo);
			add(I18N.get("Role"), jease.cms.web.system.role.Table.class,
					Images.ApplicationCertificate);
			add(I18N.get("Sequence"),
					jease.cms.web.system.sequence.Table.class,
					Images.AccessoriesCalculator);
		}
	}
}
