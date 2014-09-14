/*
    Copyright (C) 2014 maik.jablonski@jease.org

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
package jfix.search;

import java.io.Reader;

import org.apache.lucene.analysis.CharTokenizer;
import org.apache.lucene.util.Version;

public final class SimpleTokenizer extends CharTokenizer {

	public SimpleTokenizer(Reader in) {
		super(Version.LUCENE_36, in);
	}

	protected int normalize(int c) {
		return Character.toLowerCase(c);
	}

	protected boolean isTokenChar(int c) {
		return !Character.isWhitespace(c);
	}
}
