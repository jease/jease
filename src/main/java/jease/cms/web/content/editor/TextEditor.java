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

import jease.cms.domain.Text;
import jease.cms.web.component.RichTextarea;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.ZK;

import org.zkoss.codemirror.Codemirror;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;

public class TextEditor extends ContentEditor<Text> {

	RichTextarea richText = new RichTextarea();
	Codemirror plainText = new Codemirror();
	Checkbox plainMode = new Checkbox(I18N.get("Plaintext"));

	public TextEditor() {
		richText.setHeight((getDesktopHeight() / 3) + "px");
		plainText.setHeight((100 + getDesktopHeight() / 3) + "px");
		plainText.setWidth("100%");
		plainMode.addEventListener(Events.ON_CHECK, evt -> updateTextMode());
	}

	public void init() {
		add(I18N.get("Content"), richText);
		add("", new Div("text-align: right;", plainMode));
	}

	public void load() {
		richText.setText(getNode().getContent());
		plainText.setValue(getNode().getContent());
		plainMode.setChecked(getNode().isPlain());
		updateTextMode();
	}

	public void save() {
		if (richText.getParent() != null) {
			getNode().setContent(richText.getText());
		}
		if (plainText.getParent() != null) {
			getNode().setContent(plainText.getValue());
		}
		getNode().setPlain(plainMode.isChecked());
	}

	public void validate() {
	}

	private void updateTextMode() {
		if (plainMode.isChecked()) {
			plainText.setValue(richText.getText());
			if (richText.getParent() != null) {
				ZK.replace(richText, plainText);
			}
		} else {
			richText.setText(plainText.getValue());
			if (plainText.getParent() != null) {
				ZK.replace(plainText, richText);
			}
		}
	}
}
