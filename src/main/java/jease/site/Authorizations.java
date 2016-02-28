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
package jease.site;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

import jease.cms.domain.Access;
import jease.cms.domain.Content;
import jease.cms.domain.Reference;
import jfix.db4o.Database;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Service for handling authorizations via Access-Objects.
 */
public class Authorizations {

	private static Supplier<Map<Content, Access[]>> accessByContent = ConcurrentHashMap::new;

	/**
	 * Returns guarding Access object for given content or null, if content is
	 * not guarded.
	 */
	public static Access[] getGuards(Content content) {
		if (content == null) {
			return null;
		}
		Map<Content, Access[]> cache = Database.query(accessByContent);
		if (!cache.containsKey(content)) {
			List<Access> allGuards = new ArrayList<>();
			Access[] accessGuards = content.getGuards(Access.class);
			if (ArrayUtils.isNotEmpty(accessGuards)) {
				Collections.addAll(allGuards, accessGuards);
			} else {
				Reference[] referenceGuards = content.getGuards(Reference.class);
				if (ArrayUtils.isNotEmpty(referenceGuards)) {
					for (Reference reference : referenceGuards) {
						if (reference.getContent() instanceof Access) {
							allGuards.add((Access) reference.getContent());
						}
					}
				}
			}
			cache.put(content, allGuards.toArray(new Access[allGuards.size()]));
		}
		List<Access> activeGuards = new ArrayList<>();
		for (Access access : cache.get(content)) {
			if (access.isGuarding()) {
				activeGuards.add(access);
			}
		}
		if (!activeGuards.isEmpty()) {
			return activeGuards.toArray(new Access[activeGuards.size()]);
		} else {
			return getGuards((Content) content.getParent());
		}
	}

	/**
	 * Returns Access object which permits access for given basic authorization
	 * header or null otherwise.
	 */
	public static Access findAuthorizingGuard(String authorization, Access[] guards) {
		String userpass = decodeBasicAuthorization(authorization);
		if (userpass == null) {
			return null;
		}
		int index = userpass.indexOf(":");
		String login = userpass.substring(0, index);
		String password = userpass.substring(index + 1);
		for (Access guard : guards) {
			if (guard.approves(login, password)) {
				return guard;
			}
		}
		return null;
	}

	/**
	 * Returns the username from given basic authorization header.
	 */
	public static String getUsername(String authorization) {
		String userpass = decodeBasicAuthorization(authorization);
		if (userpass != null) {
			return userpass.substring(0, userpass.indexOf(":"));
		}
		return null;
	}

	private static String decodeBasicAuthorization(String authorizationHeader) {
		if (authorizationHeader == null) {
			return null;
		}
		String userpassEncoded = authorizationHeader.substring(6);
		return new String(Base64.getDecoder().decode(userpassEncoded));
	}

}
