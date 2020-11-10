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
package jease.site;

import java.util.HashMap;
import java.util.Map;

import jease.cms.domain.Access;
import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.functor.Supplier;
import jfix.util.Crypts;

public class Authorizations {

	private static Supplier<Map<Content, Access>> accessByContent = new Supplier<Map<Content, Access>>() {
		public Map<Content, Access> get() {
			return new HashMap<Content, Access>();
		}
	};

	/**
	 * Checks if content is guarded by Access-Object against
	 * HTTP-Authorization-Header. Returns null if no guard exists or
	 * authorization against guard is successful, otherwise the Access-Guard is
	 * returned, which should force an unauthorized-response.
	 */
	public static Access check(Content content, String authorizationHeader) {
		Map<Content, Access> cache = Database.query(accessByContent);
		if (!cache.containsKey(content)) {
			cache.put(content, content.getGuard(Access.class));
		}
		Access access = cache.get(content);
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

}
