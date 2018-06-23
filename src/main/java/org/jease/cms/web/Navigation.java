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
package org.jease.cms.web;

import org.jease.Names;
import org.jease.Registry;
import org.jease.cmf.web.JeaseSession;
import org.jease.cms.domain.User;
import org.jease.cms.web.content.ContentManager;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Logout;
import jfix.zk.Tabbox;

import org.apache.commons.lang3.StringUtils;
import org.jease.Names;
import org.jease.Registry;
import org.jease.cmf.web.JeaseSession;
import org.jease.cms.domain.User;
import org.jease.cms.web.content.ContentManager;
import org.jease.cms.web.system.information.Display;
import org.jease.cms.web.system.linkcheck.Table;
import org.jease.cms.web.system.trash.Control;
import org.jease.cms.web.user.SessionUserEditor;

public class Navigation extends Tabbox<Object> {

	public Navigation() {
		init();
	}

	public void init() {
		addContentTab();
		addLinkCheckTab();
		addUserTab();
		addSystemTab();
		addLogoutTab();
	}

	public void addContentTab() {
		if (JeaseSession.get(User.class).isContentManager()) {
			add(I18N.get("Content"), ContentManager.class, Images.MailAttachment);
		}
	}

	public void addLinkCheckTab() {
		if (JeaseSession.get(User.class).isContentManager()
				&& StringUtils.isNotBlank(Registry.getParameter(Names.JEASE_SITE_DESIGN))) {
			add(I18N.get("Link_Check"), Table.class, Images.NetworkTransmit);
		}
	}

	public void addUserTab() {
		if (JeaseSession.get(User.class).isAdministrator()
				|| StringUtils.isBlank(Registry.getParameter(Names.JEASE_CMS_AUTHENTICATOR))) {
			if (JeaseSession.get(User.class).isAdministrator()) {
				add(I18N.get("User"), org.jease.cms.web.user.Table.class, Images.SystemUsers);
			} else {
				add(I18N.get("User"), SessionUserEditor.class, Images.SystemUsers);
			}
		}
	}

	public void addSystemTab() {
		if (JeaseSession.get(User.class).isAdministrator()) {
			add(I18N.get("System"), System.class, Images.ApplicationsSystem);
		}
	}

	public void addLogoutTab() {
		add(I18N.get("Logout"), Logout.class, Images.SystemLogOut);
	}

	public static class System extends Tabbox<Object> {

		public System() {
			init();
		}

		public void init() {
			addParameterTab();
			addRedirectTab();
			addTrashTab();
			addRoleTab();
			addSequenceTab();
			addInfoTab();
		}

		public void addParameterTab() {
			add(I18N.get("Parameter"), org.jease.cms.web.system.parameter.Table.class, Images.PreferencesSystem);
		}

		public void addRedirectTab() {
			if (StringUtils.isNotBlank(Registry.getParameter(Names.JEASE_SITE_DESIGN))) {
				add(I18N.get("Redirect"), org.jease.cms.web.system.redirect.Table.class, Images.EmblemSymbolicLink);
			}
		}

		public void addTrashTab() {
			add(I18N.get("Trash"), Control.class, Images.UserTrashFull);
		}

		public void addRoleTab() {
			add(I18N.get("Role"), org.jease.cms.web.system.role.Table.class, Images.ApplicationCertificate);
		}

		public void addSequenceTab() {
			add(I18N.get("Sequence"), org.jease.cms.web.system.sequence.Table.class, Images.AccessoriesCalculator);
		}

		public void addInfoTab() {
			add(I18N.get("Information"), Display.class, Images.UtilitiesSystemMonitor);
		}

	}

}
