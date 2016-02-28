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

import jease.cmf.service.Nodes;
import jease.cms.domain.Discussion;
import jease.cms.service.Mails;
import jfix.util.I18N;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;

/**
 * Service for handling Discussions and Comments.
 */
public class Discussions {

	public static int MAX_SUBJECT_LENGTH = 64;
	public static int MAX_AUTHOR_LENGTH = 64;
	public static int MAX_COMMENT_LENGTH = 1024;

	/**
	 * Adds a comment with given values to given Discussion. Visible flag
	 * indicates if added comment should be visible or not.
	 * 
	 * Returns null on success, otherwise an error message.
	 */
	public static String addComment(Discussion discussion, String author,
			String subject, String comment, boolean visible) {
		if (StringUtils.isBlank(subject) || StringUtils.isBlank(author) || StringUtils.isBlank(comment)) {
			return I18N.get("All_fields_are_required");
		}
		if (subject.length() > MAX_SUBJECT_LENGTH || author.length() > MAX_AUTHOR_LENGTH || comment.length() > MAX_COMMENT_LENGTH) {
			return I18N.get("Input_is_too_long");
		}

		// Escape all user input
		subject = StringEscapeUtils.escapeHtml4(subject);
		author = StringEscapeUtils.escapeHtml4(author);
		comment = StringEscapeUtils.escapeHtml4(comment);

		// Save comment to database.
		discussion.addComment(subject, author, comment, visible);
		Nodes.save(discussion);

		// Send email for review to editor in charge.
		String recipient = discussion.getEditor().getEmail();
		if (StringUtils.isNotBlank(recipient)) {
			Mails.dispatch(recipient, recipient,
					String.format("%s (%s)", subject, author), comment);
		}

		return null;
	}
}
