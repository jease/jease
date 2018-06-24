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
package jease.cms.domain;

import java.io.IOException;

import org.apache.tika.Tika;
import org.apache.tika.exception.TikaException;

/**
 * A File which allows to extract the content as plain text and is therefore
 * indexable for full text search.
 */
public class Document extends File {

	private String text;
	private long lastTextUpdate;

	public Document() {
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getText());
	}

	public String getText() {
		if (text == null || lastTextUpdate < getFile().lastModified()) {
			try {
				Tika tika = new Tika();
				tika.setMaxStringLength(-1);
				text = tika.parseToString(getFile()).replaceAll("\n\\s*\n+",
						"\n\n");
			} catch (TikaException e) {
				text = "";
			} catch (IOException e) {
				text = "";
			} finally {
				lastTextUpdate = getFile().lastModified();
			}
		}
		return text;
	}

	public Document copy(boolean recursive) {
		Document document = (Document) super.copy(recursive);
		// Trigger conversion to plain text
		document.getText();
		return document;
	}
}
