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

import java.util.Date;

import jease.cmf.service.Nodes;
import jease.cms.domain.Folder;
import jease.cms.domain.User;
import jease.cms.service.Users;
import jease.cms.web.i18n.Strings;
import jease.cms.web.user.Editor;
import jfix.zk.Div;
import jfix.zk.Window;
import jfix.zk.ZK;

public class Setup extends Div {

	public Setup() {
		if (Nodes.getRoot() == null) {
			createRootFolder();
		}
		if (Users.queryAdministrators().size() == 0) {
			createAdministrator();
		} else {
			redirectToLogin();
		}
	}

	private void createRootFolder() {
		Folder folder = new Folder();
		folder.setId("");
		folder.setTitle(Strings.Jease);
		folder.setLastModified(new Date());
		Nodes.setRoot(folder);
		Nodes.save(folder);
	}

	private void createAdministrator() {
		User administrator = new User();
		administrator.setAdministrator(true);
		administrator.setRoots(new Folder[] { (Folder) Nodes.getRoot() });

		Editor userEditor = new Editor() {
			public void init() {
				hideButtons();
				getSaveButton().setVisible(true);
				disableAdministration();
				super.init();
			}

			public void save() {
				super.save();
				redirectToLogin();
			}
		};
		userEditor.setObject(administrator);
		userEditor.refresh();

		Window window = new Window(Strings.Setup_Administrator);
		window.appendChild(userEditor);

		getRoot().appendChild(window);
	}

	private void redirectToLogin() {
		ZK.redirect("..");
	}
}
