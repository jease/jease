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

import java.util.Date;

import jease.cmf.service.Nodes;
import jease.cms.domain.Folder;
import jease.cms.domain.Parameter;
import jease.cms.domain.User;
import jease.cms.service.Users;
import jease.cms.web.i18n.Strings;
import jease.cms.web.user.Editor;
import jfix.db4o.Database;
import jfix.zk.ActionListener;
import jfix.zk.Div;
import jfix.zk.Window;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Event;

/**
 * Initial setup of JeaseCMS: create root node and administration account.
 */
public class Setup extends Div {

	public Setup() {
		if (Database.query(Parameter.class).isEmpty()) {
			createParameter();
		}
		if (Nodes.getRoot() == null) {
			createRoot();
		}
		if (Users.queryAdministrators().isEmpty()) {
			createAdministrator();
		} else {
			redirectToLogin();
		}
	}

	private void createParameter() {
		for (Parameter parameter : new Parameter[] {
				new Parameter("JEASE_SITE_DESIGN", "bright"),
				new Parameter("JEASE_REVISION_COUNT", "10"),
				new Parameter("JEASE_REVISION_DAYS", "30") }) {
			Database.save(parameter);
		}
	}

	private void createRoot() {
		Folder folder = new Folder();
		folder.setId(ZK.getContextPath().replaceFirst("/", ""));
		folder.setTitle(Strings.Jease);
		folder.setLastModified(new Date());
		folder.setVisible(true);
		Nodes.setRoot(folder);
		Nodes.save(folder);
	}

	private void createAdministrator() {
		final User administrator = new User();
		administrator.setAdministrator(true);
		administrator.setRoots(new Folder[] { (Folder) Nodes.getRoot() });

		final Window window = new Window(Strings.Setup_Administrator);
		window.setClosable(false);
		window.setParent(getRoot());

		Editor editor = new Editor();
		editor.setObject(administrator);
		editor.addChangeListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				window.close();
				redirectToLogin();
			}
		});
		editor.refresh();
		editor.setParent(window);
	}

	private void redirectToLogin() {
		ZK.redirect("..");
	}
}
