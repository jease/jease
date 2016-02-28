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

import jease.Names;
import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseConfig;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeFilter;
import jease.cms.domain.User;
import jease.cms.service.Authenticator;
import jease.cms.service.Users;
import jease.cms.web.content.Configuration;
import jfix.db4o.Database;
import jfix.util.Reflections;
import jfix.zk.LoginWindow;
import jfix.zk.Modal;
import jfix.zk.Sessions;
import jfix.zk.Tabbox;
import jfix.zk.ZK;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.ClientInfoEvent;
import org.zkoss.zk.ui.event.Events;

/**
 * Login into JeaseCMS: init user session and display tab-navigation.
 * 
 * If a user is already stored within the session, we don't reinforce a login,
 * but recreate the desktop from scratch. This avoids showing the login page
 * when the users refreshes the browser.
 */
public class Application extends LoginWindow {

	private String queryString = ZK.getQueryString();
	private String navigationClassName = Navigation.class.getName();
	private String configurationClassName = Configuration.class.getName();

	public Application() {
		loginUser(JeaseSession.get(User.class));
	}

	public String getTitle() {
		return Executions.getCurrent().getDesktop().getFirstPage().getTitle();
	}

	public void doLogin(String login, String password) {
		loginUser(getAuthenticator().identify(login, password));
	}

	private void loginUser(User user) {
		if (user != null && !user.isDisabled() && Users.isStored(user)) {
			notifyAboutMaintenance(user);
			initSession(user);
		}
	}

	private Authenticator getAuthenticator() {
		try {
			String authenticator = Registry
					.getParameter(Names.JEASE_CMS_AUTHENTICATOR);
			if (StringUtils.isNotBlank(authenticator)) {
				return (Authenticator) Reflections.newInstance(authenticator);
			}
		} catch (RuntimeException e) {
			Modal.exception(e);
		}
		return new Authenticator();
	}

	private void initSession(User user) {
		initBrowserInfo();
		storeLastSession(user);
		JeaseSession.set(user);
		if (ArrayUtils.isNotEmpty(user.getRoots())) {
			JeaseSession.setConfig((JeaseConfig) Reflections
					.newInstance(configurationClassName));
			JeaseSession.setFilter(new NodeFilter(JeaseSession.getConfig()
					.newNodes()));
			JeaseSession.setRoots(user.getRoots());
			if (queryString != null) {
				Node node = Nodes.getByPath(queryString);
				if (JeaseSession.getFilter().isAccepted(node)) {
					JeaseSession.setContainer(node);
				}
			}
			if (JeaseSession.getContainer() == null
					|| !JeaseSession.getContainer().isDescendant(
							user.getRoots())) {
				JeaseSession.setContainer(user.getRoots()[0]);
			}
		}
		updatePageTitle(user);
		showNavigation();
	}

	private void showNavigation() {
		show((Tabbox<Object>) Reflections.newInstance(navigationClassName));
	}

	private void storeLastSession(User user) {
		user.setLastSession(new Date());
		Database.ext().persist(user);
	}

	private void initBrowserInfo() {
		getRoot().addEventListener(Events.ON_CLIENT_INFO, event -> {
			ClientInfoEvent ce = (ClientInfoEvent) event;
			JeaseSession.set(Names.JEASE_CMS_HEIGHT, ce.getDesktopHeight());
			JeaseSession.set(Names.JEASE_CMS_WIDTH, ce.getDesktopWidth());
		});
	}

	private void notifyAboutMaintenance(User user) {
		String message = Registry.getParameter(Names.JEASE_CMS_MAINTENANCE);
		if (StringUtils.isNotBlank(message)) {
			if (user.isAdministrator()) {
				Modal.info(message);
			} else {
				Modal.info(message, event -> Sessions.invalidate());
			}
		}
	}

	private void updatePageTitle(User user) {
		String status = String.format("%s@%s", user.getLogin(), getTitle());
		Executions.getCurrent().getDesktop().getFirstPage().setTitle(status);
	}

	public void setNavigation(String classname) {
		this.navigationClassName = classname;
	}

	public void setConfiguration(String classname) {
		this.configurationClassName = classname;
	}

}
