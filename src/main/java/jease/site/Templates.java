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

import java.util.function.Function;
import java.util.function.Supplier;

import jease.Names;
import jease.Registry;
import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.util.Reflections;

import org.apache.commons.lang3.StringUtils;

/**
 * Service to resolve template for content nodes.
 */
public class Templates implements Function<Content, String> {

	private static Supplier<Function<Content, String>> supplier = () -> {
		String templateResolver = Registry.getParameter(Names.JEASE_TEMPLATE_RESOLVER);
		if (StringUtils.isNotBlank(templateResolver)) {
			return (Function<Content, String>) Reflections.newInstance(templateResolver);
		}
		return new Templates();
	};

	/**
	 * Returns template path for given content.
	 */
	public static String get(Content content) {
		return Database.query(supplier).apply(content);
	}

	/**
	 * Returns template path for given content.
	 */
	public String apply(Content content) {
		return Registry.getView(content);
	}

}