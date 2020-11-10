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

import java.lang.reflect.Field;
import java.util.Date;

import jease.cmf.service.Nodes;
import jease.cms.domain.Access;
import jease.cms.domain.Content;
import jease.cms.domain.Factory;
import jease.cms.domain.Folder;
import jease.cms.domain.Parameter;
import jease.cms.domain.Role;
import jease.cms.domain.Script;
import jease.cms.domain.Transit;
import jease.cms.domain.User;
import jease.cms.domain.Version;
import jease.cms.service.Contents;
import jease.cms.service.Users;
import jease.cms.web.i18n.Strings;
import jease.cms.web.user.Editor;
import jfix.db4o.Blob;
import jfix.db4o.Database;
import jfix.functor.Functors;
import jfix.functor.Predicate;
import jfix.zk.ActionListener;
import jfix.zk.Div;
import jfix.zk.Window;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Event;

/**
 * Initial setup and upgrade for JeaseCMS.
 */
public class Setup extends Div {

	public Setup() {
		updateAccessPasswords();
		updateUserPasswords();
		updateRevisions();
		setupParameter();
		setupContent();
		setupRoles();
		updateUserRoles();
		setupAdministrator();
	}

	private void setupParameter() {
		if (Database.query(Parameter.class).isEmpty()) {
			for (Parameter parameter : new Parameter[] {
					new Parameter("JEASE_SITE_DESIGN", "bright"),
					new Parameter("JEASE_REVISION_COUNT", "10"),
					new Parameter("JEASE_REVISION_DAYS", "30") }) {
				Database.save(parameter);
			}
		}
	}

	private void setupContent() {
		if (Nodes.getRoot() == null) {
			Folder folder = new Folder();
			folder.setId(ZK.getContextPath().replaceFirst("/", ""));
			folder.setTitle(Strings.Jease);
			folder.setLastModified(new Date());
			folder.setVisible(true);
			Nodes.setRoot(folder);
			Nodes.save(folder);
		}
	}

	private void setupRoles() {
		if (Database.query(Role.class).isEmpty()) {
			Role administrator = new Role();
			administrator.setName(Strings.Administrator);
			administrator.setAdministrator(true);
			administrator.setTypes(Contents.getClassNamesForAvailableTypes());
			Database.save(administrator);

			Role editor = new Role();
			editor.setName(Strings.Editor);
			editor.setTypes(Functors.filter(
					Contents.getClassNamesForAvailableTypes(),
					new Predicate<String>() {
						public boolean test(String type) {
							return !(type.equals(Factory.class.getName())
									|| type.equals(Script.class.getName()) || type
									.equals(Transit.class.getName()));
						}
					}));
			Database.save(editor);
		}
	}

	private void setupAdministrator() {
		if (Users.queryAdministrators().isEmpty()) {
			final User administrator = new User();
			administrator.setRole(getRole(Strings.Administrator));
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
		} else {
			redirectToLogin();
		}
	}

	private void redirectToLogin() {
		ZK.redirect("..");
	}

	private void updateRevisions() {
		try {
			Field revisionsField = Content.class.getDeclaredField("revisions");
			revisionsField.setAccessible(true);
			for (Content content : Database.query(Content.class)) {
				Blob[] revisions = (Blob[]) revisionsField.get(content);
				if (revisions != null) {
					Version[] versions = new Version[revisions.length];
					for (int i = 0; i < revisions.length; i++) {
						versions[i] = new Version(null, revisions[i]);
					}
					revisionsField.set(content, null);
					content.setRevisions(versions);
					Database.save(content);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}

	private void updateUserRoles() {
		if (Users.queryAdministrators().isEmpty()) {
			try {
				for (User user : Database.query(User.class)) {
					if (user.getRole() == null) {
						Field administratorField = User.class
								.getDeclaredField("administrator");
						administratorField.setAccessible(true);
						Boolean admin = (Boolean) administratorField.get(user);
						if (admin != null && admin.booleanValue()) {
							user.setRole(getRole(Strings.Administrator));
						} else {
							user.setRole(getRole(Strings.Editor));
						}
						Database.save(user);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
	}

	private void updateUserPasswords() {
		for (User user : Database.query(User.class)) {
			if (!user.getPassword().endsWith("==")) {
				String password = user.getPassword();
				user.setPassword(null);
				user.setPassword(password);
				Database.save(user);
			}
		}
	}

	private void updateAccessPasswords() {
		for (Access access : Database.query(Access.class)) {
			if (!access.getPassword().endsWith("==")) {
				String password = access.getPassword();
				access.setPassword(null);
				access.setPassword(password);
				Database.save(access);
			}
		}
	}

	private Role getRole(final String name) {
		return Database.queryUnique(Role.class, new Predicate<Role>() {
			public boolean test(Role role) {
				return role.getName().equals(name);
			}
		});
	}
}
