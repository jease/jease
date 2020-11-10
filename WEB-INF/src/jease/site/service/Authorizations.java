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
package jease.site.service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import jease.cms.domain.Access;
import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.util.Crypts;

public class Authorizations {

	/**
	 * Checks if content is protected by Access-Object against
	 * HTTP-Authorization-Header. Returns null if access is granted,
	 * otherwise the nearest Access-Object is returned, which should force an
	 * unauthorized-response.
	 */
	public static Access check(Content content, String authorizationHeader) {
		Access access = findNearestAccessForContent(content);
		if (access != null) {
			String userpass = Crypts
					.decodeBasicAuthorization(authorizationHeader);
			if (userpass == null
					|| !userpass.equals(access.getLogin() + ":"
							+ access.getPassword())) {
				return access;
			}
		}
		return null;
	}

	private static Access findNearestAccessForContent(Content content) {
		// Sort acl by length of path, so we start checking access rules with
		// deepest access rule first.
		List<Access> acl = Database.query(Access.class);
		Collections.sort(acl, new Comparator<Access>() {
			public int compare(Access o1, Access o2) {
				return o2.getPath().length() - o1.getPath().length();
			}
		});
		String contentPath = content.getPath();
		for (Access access : acl) {
			if (contentPath.startsWith(access.getParent().getPath())) {
				return access;
			}
		}
		return null;
	}
}
