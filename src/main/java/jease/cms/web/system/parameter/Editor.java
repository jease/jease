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
package jease.cms.web.system.parameter;

import java.util.function.Predicate;

import org.apache.commons.lang3.StringUtils;
import org.sinnlabs.zk.ui.CodeMirror;
import org.zkoss.zul.Textbox;

import jease.cms.domain.Parameter;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.ObjectEditor;

public class Editor extends ObjectEditor<Parameter> {

    Textbox key = new Textbox();
    Textbox singleValue = new Textbox();
    CodeMirror multiValue = new CodeMirror();

    public Editor() {
        multiValue.setHeight(getPlainEditorHeight());
    }

    @Override
    public void init() {
        add(I18N.get("Key"), key);
        add(I18N.get("Value"), new Div(singleValue, multiValue));
    }

    @Override
    public void load() {
        boolean multiline = StringUtils.isBlank(getObject().getValue())
                || getObject().getValue().contains("\n");
        key.setText(getObject().getKey());
        singleValue.setText(getObject().getValue());
        singleValue.setVisible(!multiline);
        multiValue.setValue(getObject().getValue());
        multiValue.setVisible(multiline);
        adjustSyntax();
    }

    private void adjustSyntax() {
        if (multiValue.getValue().startsWith("<")) {
            multiValue.setMode(CodeMirror.TEXT_HTML);
            return;
        }
        multiValue.setMode(CodeMirror.TEXT_PLAIN);
    }

    @Override
    public void save() {
        getObject().setKey(key.getText());
        if (singleValue.isVisible()) {
            getObject().setValue(singleValue.getText());
        } else {
            getObject().setValue(multiValue.getValue());
        }
        Database.save(getObject());
    }

    @Override
    public void delete() {
        Database.delete(getObject());
    }

    @Override
    public void validate() {
        validate(StringUtils.isEmpty(key.getValue()), I18N.get("Key_is_required"));
        validate(!Database.isUnique(getObject(), new Predicate<Parameter>() {
            @Override
            public boolean test(Parameter parameter) {
                return parameter.getKey().equals(key.getText());
            }
        }), I18N.get("Key_must_be_unique"));
    }
}
