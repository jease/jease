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
package jease.cms.web.content.editor;

import jease.cms.domain.Script;
import jease.cms.web.i18n.Strings;
import jfix.zk.Textarea;

public class ScriptEditor extends ContentEditor<Script> {

	Textarea code = new Textarea();

	public ScriptEditor() {
		code.setHeight("350px");
	}

	public void init() {
		add(Strings.Code, code);
	}

	public void load() {
		code.setText(getNode().getCode());
	}

	public void save() {
		getNode().setCode(code.getText());
	}

	public void validate() {
	}

}