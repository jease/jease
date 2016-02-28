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

import jease.cms.domain.Document;
import jfix.util.I18N;
import jfix.zk.Images;
import jfix.zk.Modal;
import jfix.zk.Window;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Textbox;

public class DocumentEditor extends FileEditor<Document> {

	Button showText = new Button(I18N.get("Text"), Images.EditFind);

	public DocumentEditor() {
		showText.addEventListener(Events.ON_CLICK, event -> showText());
		getButtons().appendChild(showText);
	}

	private void showText() {
		Document currentNode = getNode();
		try {
			copyObject();
			saveEditorToObject();
			getNode().setParent(null);
			Textbox textarea = new Textbox();
			textarea.setRows(3);
			textarea.setVflex("1");
			textarea.setHflex("1");
			textarea.setValue(getObject().getText());
			Window window = new Window(getObject().getPath());
			window.setHeight("75%");
			window.appendChild(textarea);
			window.doModal();
			getRoot().appendChild(window);
		} catch (Exception e) {
			Modal.exception(e);
			return;
		} finally {
			setObject(currentNode);
		}
	}

	public void save() {
		super.save();
		// Trigger conversion to plain text
		getObject().getText();
	}
}
