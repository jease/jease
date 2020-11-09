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
package jease.cmf.web;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import jfix.db4o.Database;

public class JeaseServletListener implements ServletContextListener {

	public void contextInitialized(ServletContextEvent sce) {
		Database.open(sce.getServletContext().getInitParameter("JEASE_DATABASE_NAME"));
	}

	public void contextDestroyed(ServletContextEvent sce) {
		Database.close();
	}
}
