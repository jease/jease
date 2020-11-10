/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import jease.cmf.service.Filenames;
import jease.cms.domain.Script;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Checkbox;
import jfix.zk.Codearea;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.InputEvent;

public class ScriptEditor extends ContentEditor<Script> {

	Codearea code = new Codearea();
	Checkbox forward = new Checkbox();
	
	public ScriptEditor() {
		code.setHeight((100 + getDesktopHeight() / 3) + "px");
		id.addChangingListener(new ActionListener() {			
			public void actionPerformed(Event event) {
				code.setSyntax(Filenames.asExtension(((InputEvent) event).getValue()));
			}
		});
	}

	public void init() {
		add(I18N.get("Code"), code);
		add(I18N.get("Forward"), forward);
	}

	public void load() {
		code.setSyntax(Filenames.asExtension(id.getValue()));
		code.setText(getNode().getCode());
		forward.setChecked(getNode().isForward());
	}

	public void save() {
		getNode().setCode(code.getText());
		getNode().setForward(forward.isChecked());
	}

	public void validate() {
	}

}
