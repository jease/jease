/*
    Copyright (C) 2013 maik.jablonski@jease.org

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

import jease.Names;
import jease.Registry;
import jease.cmf.service.Compilers;
import jease.cms.domain.Content;
import jfix.db4o.Database;
import jfix.functor.Function;
import jfix.functor.Supplier;

import org.apache.commons.lang3.StringUtils;

/**
 * Service to resolve template for content nodes.
 */
public class Templates implements Function<Content, String> {

	private static Supplier<Function<Content, String>> supplier = new Supplier<Function<Content, String>>() {
		public Function<Content, String> get() {
			String templateResolver = Registry
					.getParameter(Names.JEASE_TEMPLATE_RESOLVER);
			if (StringUtils.isNotBlank(templateResolver)) {
				return (Function<Content, String>) Compilers
						.eval(templateResolver);
			}
			return new Templates();
		}
	};

	/**
	 * Returns template path for given content.
	 */
	public static String get(Content content) {
		return Database.query(supplier).evaluate(content);
	}

	/**
	 * Returns template path for given content.
	 */
	public String evaluate(Content content) {
		return Registry.getView(content);
	}

}