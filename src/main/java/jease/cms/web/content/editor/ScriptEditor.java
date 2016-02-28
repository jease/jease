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

import jease.cms.domain.Script;
import jfix.util.I18N;

import org.apache.commons.io.FilenameUtils;
import org.zkoss.codemirror.Codemirror;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.InputEvent;
import org.zkoss.zul.Checkbox;

public class ScriptEditor extends ContentEditor<Script> {

	Codemirror code = new Codemirror();
	Checkbox forward = new Checkbox();

	public ScriptEditor() {
		code.setHeight((100 + getDesktopHeight() / 3) + "px");
		code.setConfig("lineNumbers:true");
		id.addEventListener(Events.ON_CHANGING, event -> code
				.setSyntax(FilenameUtils.getExtension(((InputEvent) event)
						.getValue())));
	}

	public void init() {
		add(I18N.get("Code"), code);
		add(I18N.get("Forward"), forward);
	}

	public void load() {
		code.setSyntax(FilenameUtils.getExtension(id.getValue()));
		code.setValue(getNode().getCode());
		forward.setChecked(getNode().isForward());
	}

	public void save() {
		getNode().setCode(code.getValue());
		getNode().setForward(forward.isChecked());
	}

	public void validate() {
	}

}
