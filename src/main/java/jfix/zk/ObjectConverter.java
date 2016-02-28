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
package jfix.zk;

import java.util.Collection;
import java.util.Date;

public class ObjectConverter {

	private static String delimiter = "\n";

	private ObjectConverter() {
	}

	public static Object convert(Object value) {
		if (value == null) {
			return "";
		}

		if (value instanceof Number) {
			return value;
		}

		if (value instanceof Date) {
			String result = String.format("%1$tF %1$tR", value);
			int index = result.lastIndexOf(" 00:00");
			if (index == -1) {
				return result;
			} else {
				return result.substring(0, index);
			}
		}

		if (value instanceof View) {
			return convert(((View<?>) value).getValue());
		}

		if (value instanceof Collection) {
			return convert(((Collection<?>) value).toArray());
		}

		if (value.getClass().isArray()) {
			StringBuilder sb = new StringBuilder(256);
			for (Object obj : (Object[]) value) {
				if (sb.length() != 0) {
					sb.append(delimiter);
				}
				sb.append(convert(obj));
			}
			return sb.toString();
		}

		return String.valueOf(value);
	}

}
