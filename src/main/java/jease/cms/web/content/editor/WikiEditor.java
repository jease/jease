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
package jease.cms.web.content.editor;

import jease.cms.domain.Wiki;
import jfix.util.I18N;

import org.zkoss.codemirror.Codemirror;

public class WikiEditor extends ContentEditor<Wiki> {

	Codemirror content = new Codemirror();

	public WikiEditor() {
		content.setHeight((100 + getDesktopHeight() / 3) + "px");
		content.setSyntax("wiki");
	}

	public void init() {
		add(I18N.get("Content"), content);
	}

	public void load() {
		content.setValue(getNode().getContent());
	}

	public void save() {
		getNode().setContent(content.getValue());
	}

	public void validate() {
	}
}
