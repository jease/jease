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

import java.util.ArrayList;
import java.util.List;

import jease.Registry;
import jease.cmf.service.Revisioner;
import jease.cms.domain.Content;
import jease.cms.domain.Version;

/**
 * Service to create/restore content-revisions. Each content revision is stored
 * within an array (newest revision is first element of array), so overhead is
 * neglectable.
 * 
 * The minimum number of revisions to be kept is returned by {@link #getCount()}
 * (default: -1 = unlimited, 0 = ignore).
 * 
 * The number of days for which revisions in the past should be kept is returned
 * by {@link #getDays()} (default: -1 = unlimited, 0 = ignore).
 */
public class Revisions {

	/**
	 * Minimum count of revisions to be kept. -1 means unlimited.
	 */
	public static int getCount() {
		try {
			return Integer.parseInt(Registry.getParameter("JEASE_REVISION_COUNT"));
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	/**
	 * Number of days in the past for which revisions should be kept. -1 means
	 * unlimited.
	 */
	public static int getDays() {
		try {
			return Integer.parseInt(Registry.getParameter("JEASE_REVISION_DAYS"));
		} catch (NumberFormatException e) {
			return -1;
		}

	}

	private final static Revisioner revisioner = new Revisioner(
			Contents.getAvailableTypes());

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
		purge(content, getCount(), getDays());
	}

	/**
	 * Returns the given revision (by index) for given content.
	 */
	public static <E extends Content> E checkout(E content, int revision) {
		return (E) revisioner.fromBlob(content.getRevisions()[revision]
				.getBlob());
	}

	/**
	 * Keeps only revisions for given content below given count and given days.
	 * Returns number of purged revisions. Setting count or days to 0 means to
	 * ignore it, -1 means unlimited.
	 */
	public static int purge(Content content, int count, int days) {
		if (count == -1 || days == -1 || content.getRevisions() == null) {
			return 0;
		}
		int revisionsBefore = content.getRevisions().length;
		long daysInPast = System.currentTimeMillis()
				- (days * 24L * 3600L * 1000L);
		List<Version> revisions = new ArrayList();
		for (Version version : content.getRevisions()) {
			if ((count > 0 && revisions.size() < count)
					|| (days > 0 && version.getBlob().getFile().lastModified() > daysInPast)) {
				revisions.add(version);
			}
		}
		content.setRevisions(revisions.toArray(new Version[] {}));
		return revisionsBefore - content.getRevisions().length;
	}
}
