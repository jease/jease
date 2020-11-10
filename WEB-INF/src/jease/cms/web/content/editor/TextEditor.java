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

import jease.cms.domain.Text;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Checkbox;
import jfix.zk.Codearea;
import jfix.zk.Div;
import jfix.zk.RichTextarea;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Event;

public class TextEditor extends ContentEditor<Text> {

	RichTextarea richText = new RichTextarea();
	Codearea plainText = new Codearea();
	Checkbox plainMode = new Checkbox(Strings.Plaintext);

	public TextEditor() {
		richText.setHeight("350px");
		plainText.setHeight("350px");
		plainText.setWidth("100%");
		plainMode.addCheckListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				updateTextMode();
			}
		});
	}

	public void init() {
		add(Strings.Content, richText);
		add("", new Div("text-align: right;", plainMode));
	}

	public void load() {
		richText.setText(getNode().getContent());
		plainText.setText(getNode().getContent());
		plainMode.setChecked(getNode().isPlain());
		updateTextMode();
	}

	public void save() {		
		if (richText.getParent() != null) {
			getNode().setContent(richText.getText());
		}
		if (plainText.getParent() != null) {
			getNode().setContent(plainText.getText());
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
			richText.setText(plainText.getText());
			if (plainText.getParent() != null) {
				ZK.replace(plainText, richText);
			}
		}
	}
}
