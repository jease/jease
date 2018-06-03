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
package jease.cms.web.servlet;

import java.util.Locale;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import jease.Names;
import jease.cms.service.Timers;
import jfix.db4o.Database;
import jfix.util.I18N;

import org.apache.commons.lang3.StringUtils;

@WebListener
public class JeaseServletListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
		ServletContext context = sce.getServletContext();
		initSystemProperties(context);
		initLocale(context);
		initDatabase(context);
		initTimer(context);
	}

	public void contextDestroyed(ServletContextEvent sce) {
		stopTimer();
		closeDatabase();
	}

	protected void initSystemProperties(ServletContext context) {
		System.setProperty("networkaddress.cache.ttl", "500");
	}

	protected void initLocale(ServletContext context) {
		String localeCode = context
				.getInitParameter(Names.JEASE_DEFAULT_LOCALE);
		if (StringUtils.isNotBlank(localeCode)) {
			Locale locale = new Locale(localeCode);
			I18N.load(locale);
			context.setAttribute("org.zkoss.web.preferred.locale", locale);
		}
	}

	protected void initDatabase(ServletContext context) {
		String databaseName = context
				.getInitParameter(Names.JEASE_DATABASE_NAME);
		if (databaseName != null) {
			Database.open(databaseName);
		} else {
			throw new RuntimeException();
		}
	}

	protected void closeDatabase() {
		Database.close();
	}

	protected void initTimer(ServletContext context) {
		Timers.start();
	}

	protected void stopTimer() {
		Timers.stop();
	}

}
