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
package jease.cms.web.content.editor.property;

import jease.cms.domain.property.ScriptProperty;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Images;
import jfix.zk.Modal;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class ScriptPropertyEditor extends Div implements
		PropertyEditor<ScriptProperty> {

	private Textbox classarea = new Textbox();
	private ScriptProperty property;

	public ScriptPropertyEditor() {
		classarea.setWidth("100%");
		Button execute = new Button(I18N.get("Execute"),
				Images.MediaPlaybackStart);
		execute.addEventListener(Events.ON_CLICK, $event -> executePerformed());
		appendChild(classarea);
		appendChild(execute);
	}

	public ScriptProperty getProperty() {
		property.setCode(classarea.getValue());
		return property;
	}

	public void setProperty(ScriptProperty property) {
		this.property = property;
		this.classarea.setValue(property.getCode());
	}

	private void executePerformed() {
		ScriptProperty clone = property.copy();
		clone.setCode(classarea.getValue());
		Modal.info(clone.toString());
	}

}
