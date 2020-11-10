/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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

import jease.cms.domain.*;
import jease.cms.web.i18n.*;
import jfix.zk.*;

import org.zkoss.zk.ui.event.*;

public class TextEditor extends ContentEditor<Text> {

	RichTextarea richText = new RichTextarea();
	Textarea plainText = new Textarea();
	Checkbox plainMode = new Checkbox(Strings.Plaintext);

	public TextEditor() {
		plainText.setHeight("300px");
		plainMode.addCheckListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				updateTextMode();
			}
		});
	}

	public void init() {
		add(Strings.Content, new Column(richText, plainText, new Div(plainMode,
				"text-align: right;")));
	}

	public void load() {
		richText.setText(getNode().getContent());
		plainText.setText(getNode().getContent());
		plainMode.setChecked(getNode().isPlain());
		updateTextMode();
	}

	public void save() {
		getNode().setPlain(plainMode.isChecked());
		if (plainMode.isChecked()) {
			getNode().setContent(plainText.getText());
		} else {
			getNode().setContent(richText.getText());
		}
	}

	public void validate() {
	}

	private void updateTextMode() {
		if (plainMode.isChecked()) {
			plainText.setText(richText.getText());
			plainText.setVisible(true);
			richText.setVisible(false);
		} else {
			richText.setText(plainText.getText());
			richText.setVisible(true);
			plainText.setVisible(false);
		}
	}
}
