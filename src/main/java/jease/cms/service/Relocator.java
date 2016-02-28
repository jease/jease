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

import java.util.List;
import java.util.function.Predicate;

import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Content.PathChangeProcessor;
import jease.cms.domain.Trash;
import jfix.db4o.Database;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Implements a PathChangeProcessor which automatically rewrites internal links
 * in content.
 */
public class Relocator implements PathChangeProcessor {

	public void process(String oldPath, String newPath) {
		if (!oldPath.equals(newPath)
				&& ArrayUtils.isEmpty(Nodes.getByPath(newPath).getParents(
						Trash.class))) {
			final String target = "./~" + oldPath;
			final String replacement = "./~" + newPath;
			Database.write(new Runnable() {
				public void run() {
					for (Content content : searchContent(target)) {
						content.replace(target, replacement);
						Database.save(content);
					}
				}
			});
		}
	}

	private List<Content> searchContent(final String target) {
		return Database.query(Content.class, new Predicate<Content>() {
			public boolean test(Content obj) {
				return obj.getFulltext().indexOf(target) != -1;
			}
		});
	}
}
