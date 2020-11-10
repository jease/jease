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
package jease.cms.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import jease.cms.domain.Content;
import jease.cms.domain.Linkcheck;
import jfix.db4o.Database;
import jfix.functor.Command;
import jfix.util.Regexps;
import jfix.util.Urls;

public class Linkchecker implements Runnable {

	private static int TIMEOUT = 60;
	private static boolean active = false;

	public static synchronized boolean isActive() {
		return active;
	}

	public static synchronized void start() {
		new Thread(new Linkchecker()).start();
	}

	public synchronized void run() {
		if (active == false) {
			active = true;
			Database.write(new Command() {
				public void run() {
					for (Linkcheck linkstate : Database.query(Linkcheck.class)) {
						Database.delete(linkstate);
					}
				}
			});
			Map<String, Integer> linkStates = new HashMap();
			for (Content content : Database.query(Content.class)) {
				for (String url : new HashSet<String>(
						Regexps.extractUrls(content.getFulltext().toString()))) {
					url = url.replace("&amp;", "&");
					int status;
					if (linkStates.containsKey(url)) {
						status = linkStates.get(url);
					} else {
						status = Urls.getStatus(url, TIMEOUT);
						linkStates.put(url, status);
					}
					Database.save(new Linkcheck(content.getPath(), url, status));
				}
			}
			active = false;
		}
	}

}
