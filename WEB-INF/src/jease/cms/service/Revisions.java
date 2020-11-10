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
package jease.cms.service;

import java.util.Arrays;

import jease.cmf.service.Revisioner;
import jease.cms.domain.Content;
import jease.cms.domain.Version;

/**
 * Service to create/restore content-revisions. Each content revision is stored
 * within an array (newest revision is first element of array), so
 * overhead is neglectable. The number of revisions to be kept is stored within
 * MAX_REVISIONS (default: Integer.MAX_VALUE).
 */
public class Revisions {

	private final static Revisioner revisioner = new Revisioner(
			Contents.getAvailableTypes());

	/**
	 * Limit for the number ot revisions to be kept.
	 */
	public static int MAX_REVISIONS = Integer.MAX_VALUE;

	/**
	 * Add new revision for given content.
	 */
	public static void checkin(Content content) {
		checkin(null, content);
	}

	/**
	 * Add new revision with given info (e.g. username) for given content.
	 */
	public static void checkin(String info, Content content) {
		content.addRevision(new Version(info, revisioner.toBlob(content)));
		if (content.getRevisions().length > MAX_REVISIONS) {
			content.setRevisions(Arrays.copyOf(content.getRevisions(),
					MAX_REVISIONS));
		}
	}

	/**
	 * Returns the given revision (by index) for given content.
	 */
	public static <E extends Content> E checkout(E content, int revision) {
		return (E) revisioner.fromBlob(content.getRevisions()[revision]
				.getBlob());
	}
}
