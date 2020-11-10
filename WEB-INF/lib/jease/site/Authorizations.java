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
package jease.site;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jease.cms.domain.Access;
import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.functor.Supplier;
import jfix.util.Crypts;
import jfix.util.Validations;

/**
 * Service for handling authorizations via Access-Objects.
 */
public class Authorizations {

	// Needed as null-wrapper for ConcurrentHashMap
	private static final Access NO_ACCESS_GUARD = new Access();

	private static Supplier<Map<Content, Access>> accessByContent = new Supplier<Map<Content, Access>>() {
		public Map<Content, Access> get() {
			return new ConcurrentHashMap<Content, Access>();
		}
	};

	/**
	 * Checks if given content is guarded by Access-Object against
	 * HTTP-Authorization-Header. Returns null if no guard exists or
	 * authorization against guard is successful, otherwise the Access-Guard is
	 * returned, which should force an unauthorized-response.
	 */
	public static Access check(Content content, String authorizationHeader) {
		Access access = getGuard(content);
		if (access != null) {
			String userpass = Crypts
					.decodeBasicAuthorization(authorizationHeader);
			if (userpass == null) {
				return access;
			}
			int index = userpass.indexOf(":");
			String login = userpass.substring(0, index);
			String password = userpass.substring(index + 1);
			if (!(Validations.equals(access.getLogin(), login) && access
					.hasPassword(password))) {
				return access;
			}
		}
		return null;
	}

	/**
	 * Returns guarding Access object for given content or null, if content is
	 * not guarded.
	 */
	public static Access getGuard(Content content) {
		Map<Content, Access> cache = Database.query(accessByContent);
		if (!cache.containsKey(content)) {
			Content guard = content.getGuard(Access.class);
			if (guard != null) {
				cache.put(content, content.getGuard(Access.class));
			} else {
				cache.put(content, NO_ACCESS_GUARD);
			}
		}
		Access access = (Access) cache.get(content);
		return access != NO_ACCESS_GUARD ? access : null;
	}

	/**
	 * Returns true if content is guarded by an Access object.
	 */
	public static boolean isGuarded(Content content) {
		return getGuard(content) != null;
	}

}
