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

import org.sinnlabs.zk.ui.CodeMirror;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;

import jease.cms.domain.Text;
import jease.cms.web.component.RichTextarea;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.ZK;

public class TextEditor extends ContentEditor<Text> {

    RichTextarea richText = new RichTextarea();
    CodeMirror plainText = new CodeMirror();
    Checkbox plainMode = new Checkbox(I18N.get("Plaintext"));
    Checkbox showLineNums = new Checkbox(I18N.get("Show_line_numbers"));

    public TextEditor() {
        richText.setHeight(getRichEditorHeight());
        plainText.setHeight(getPlainEditorHeight());
        plainText.setWidth("100%");
        plainMode.addEventListener(Events.ON_CHECK, evt -> updateTextMode());

        showLineNums.addEventListener(Events.ON_CHECK, evt -> {
            boolean v = plainText.getLineNumbers();
            plainText.setLineNumbers(!v);
        });
        compactHeader = true;
    }

    @Override
    public void init() {
        Label label = new Label(I18N.get("Content"));
        label.setStyle("font-weight: bold; float: left; margin-top: 3px");
        showLineNums.setStyle("margin-right: 15px");
        Div h = new Div("text-align: right", label, showLineNums, plainMode);
        add(h);
        add(richText);
    }

    @Override
    public void load() {
        richText.setText(getNode().getContent());
        plainText.setValue(getNode().getContent());
        plainMode.setChecked(getNode().isPlain());
        showLineNums.setChecked(plainText.getLineNumbers());
        updateTextMode();
    }

    @Override
    public void save() {
        if (richText.getParent() != null) {
            getNode().setContent(richText.getText());
        }
        if (plainText.getParent() != null) {
            getNode().setContent(plainText.getValue());
        }
        getNode().setPlain(plainMode.isChecked());
    }

    @Override
    public void validate() {
    }

    private void updateTextMode() {
        if (plainMode.isChecked()) {
            plainText.setValue(richText.getText());
            if (richText.getParent() != null) {
                ZK.replace(richText, plainText);
            }
            showLineNums.setVisible(true);
        } else {
            richText.setText(plainText.getValue());
            if (plainText.getParent() != null) {
                ZK.replace(plainText, richText);
            }
            showLineNums.setVisible(false);
        }
    }
}
