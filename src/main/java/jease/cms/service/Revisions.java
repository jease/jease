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
package jease.cms.service;

import java.util.ArrayList;
import java.util.List;

import jease.Names;
import jease.Registry;
import jease.cmf.service.Nodes;
import jease.cmf.service.Revisioner;
import jease.cms.domain.Content;
import jease.cms.domain.Version;
import jfix.db4o.Database;

import org.apache.commons.lang3.math.NumberUtils;

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
		return NumberUtils.toInt(
				Registry.getParameter(Names.JEASE_REVISION_COUNT), -1);
	}

	/**
	 * Number of days in the past for which revisions should be kept. -1 means
	 * unlimited.
	 */
	public static int getDays() {
		return NumberUtils.toInt(
				Registry.getParameter(Names.JEASE_REVISION_DAYS), -1);
	}

	/**
	 * Returns true if getDays() or getCount() is not null.
	 */
	public static boolean isConfigured() {
		return getCount() != 0 || getDays() != 0;
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
		List<Version> revisions = new ArrayList<Version>();
		for (Version version : content.getRevisions()) {
			if ((count > 0 && revisions.size() < count)
					|| (days > 0 && version.getBlob().getFile().lastModified() > daysInPast)) {
				revisions.add(version);
			}
		}
		content.setRevisions(revisions.toArray(new Version[] {}));
		return revisionsBefore - content.getRevisions().length;
	}

	/**
	 * Keeps only revisions for given contents of given class for given count
	 * and given days. Returns number of purged revisions. Setting count or days
	 * to 0 means to ignore it, -1 means unlimited.
	 */
	public static int purge(Class<Content> clazz, int count, int days) {
		int result = 0;
		for (Content content : Nodes.getRoot().getDescendants(clazz)) {
			result += Revisions.purge(content, count, days);
			Nodes.save(content);
		}
		Database.ext().gc();
		return result;
	}

	/**
	 * Returns the number of revisions for given content class.
	 */
	public static int getNumber(Class<? extends Content> clazz) {
		int number = 0;
		for (Content content : Database.query(clazz)) {
			if (content.getRevisions() != null) {
				number += content.getRevisions().length;
			}
		}
		return number;
	}
}
