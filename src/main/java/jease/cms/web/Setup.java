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
package jease.cms.web;

import java.util.Date;
import java.util.stream.Stream;

import jease.Names;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.Factory;
import jease.cms.domain.Folder;
import jease.cms.domain.Parameter;
import jease.cms.domain.Role;
import jease.cms.domain.Script;
import jease.cms.domain.Text;
import jease.cms.domain.Transit;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.service.Users;
import jease.cms.web.user.Editor;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Window;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Events;

/**
 * Initial setup and upgrade for JeaseCMS.
 */
public class Setup extends Div {

	public Setup() {
		init();
	}

	public void init() {
		setupParameter();
		setupContent();
		setupRoles();
		setupAdministrator();
	}

	public void setupParameter() {
		if (Database.query(Parameter.class).isEmpty()) {
			for (Parameter parameter : new Parameter[] {
					new Parameter(Names.JEASE_SITE_DESIGN, "simple"),
					new Parameter(Names.JEASE_REVISION_COUNT, "10"),
					new Parameter(Names.JEASE_REVISION_DAYS, "30") }) {
				Database.save(parameter);
			}
		}
	}

	public void setupContent() {
		if (Nodes.getRoot() == null) {
			Folder folder = new Folder();
			folder.setId("");
			folder.setTitle(I18N.get("JeaseCMS"));
			folder.setLastModified(new Date());
			folder.setVisible(true);
			Nodes.setRoot(folder);
			Nodes.save(folder);

			Text text = new Text();
			text.setId("index.html");
			text.setTitle("Welcome to Jease!");
			text.setContent("<h2>This page was automatically created by the setup process.</h2><p>Feel free to <i>edit</i> or <b>delete</b> it.</p>");
			text.setLastModified(new Date());
			text.setParent(folder);
			Nodes.save(text);
		}
	}

	public void setupRoles() {
		if (Database.query(Role.class).isEmpty()) {
			Role administrator = new Role();
			administrator.setName(I18N.get("Administrator"));
			administrator.setAdministrator(true);
			administrator.setTypes(Contents.getClassNamesForAvailableTypes());
			Database.save(administrator);

			Role editor = new Role();
			editor.setName(I18N.get("Editor"));
			editor.setTypes(Stream
					.of(Contents.getClassNamesForAvailableTypes())
					.filter(type ->
							!(type.equals(Factory.class.getName())
							|| type.equals(Script.class.getName())
							|| type.equals(Transit.class.getName()))
					)
					.toArray(String[]::new));
			Database.save(editor);
		}
	}

	public void setupAdministrator() {
		if (Users.queryAdministrators().isEmpty()) {
			Role admin = Database.queryUnique(Role.class, role -> role
					.getName().equals(I18N.get("Administrator")));
			final User administrator = new User();
			administrator.setRole(admin);
			administrator.setRoots(new Folder[] { (Folder) Nodes.getRoot() });

			final Window window = new Window(I18N.get("Setup_Administrator"));
			window.setClosable(false);
			window.setParent(getRoot());

			final Editor editor = new Editor();
			editor.setObject(administrator);
			editor.addEventListener(Events.ON_CHANGE, event -> {
				window.close();
				JeaseSession.set(administrator);
				redirectToLogin();
			});
			editor.refresh();
			editor.setParent(window);
		} else {
			redirectToLogin();
		}
	}

	public void redirectToLogin() {
		ZK.redirect("/cms");
	}
}
