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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;

import jease.cmf.service.Filenames;
import jease.cms.domain.Script;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Codearea;

public class ScriptEditor extends ContentEditor<Script> {

	Codearea code = new Codearea();

	public ScriptEditor() {
		code.setHeight("350px");
		id.addChangingListener(new ActionListener() {			
			public void actionPerformed(Event event) {
				code.setSyntax(Filenames.asExtension(((InputEvent) event).getValue()));
			}
		});
	}

	public void init() {
		add(Strings.Code, code);
	}

	public void load() {
		code.setSyntax(Filenames.asExtension(id.getValue()));
		code.setText(getNode().getCode());
	}

	public void save() {
		getNode().setCode(code.getText());
	}

	public void validate() {
	}

}
