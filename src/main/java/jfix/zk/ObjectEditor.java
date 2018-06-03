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
package jfix.zk;

import jfix.util.I18N;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;

public abstract class ObjectEditor<E> extends Formbox implements Objectable<E>,
		Refreshable {

	private E object;
	private boolean initialized = false;
	private StringBuilder errorMessage = new StringBuilder();
	private Button saveButton = new Button(I18N.get("Save"),
			Images.DocumentSave);
	private Button copyButton = new Button(I18N.get("Copy"), Images.EditCopy);
	private Button deleteButton = new Button(I18N.get("Delete"),
			Images.UserTrashFull);
	private Button loadButton = new Button(I18N.get("Refresh"),
			Images.ViewRefresh);
	private Hbox buttons = new Hbox();

	public abstract void init() throws Exception;

	public abstract void load() throws Exception;

	public abstract void save() throws Exception;

	public abstract void delete() throws Exception;

	public abstract void validate() throws Exception;

	public void copy() throws Exception {
	}

	public ObjectEditor() {
		saveButton.addEventListener(Events.ON_CLICK, $event -> {
			invokeSave();
			if (errorMessage.length() == 0) {
				exit();
			}
		});
		copyButton.addEventListener(Events.ON_CLICK, $event -> invokeCopy());
		deleteButton
				.addEventListener(Events.ON_CLICK, $event -> invokeDelete());
		loadButton.addEventListener(Events.ON_CLICK, $event -> invokeLoad());
		buttons.appendChild(saveButton);
		buttons.appendChild(copyButton);
		buttons.appendChild(deleteButton);
		buttons.appendChild(loadButton);
	}

	public void refresh() {
		invokeInit();
		invokeLoad();
	}

	protected void invokeInit() {
		try {
			if (!initialized) {
				doInit();
				add("", buttons);
				initialized = true;
			}
		} catch (Exception e) {
			Modal.exception(e);
		}
	}

	protected void invokeLoad() {
		try {
			synchronized (getMonitor()) {
				doLoad();
			}
		} catch (Exception e) {
			Modal.exception(e);
		}
		disableButtons(false);
		focus();
	}

	protected void invokeSave() {
		try {
			errorMessage = new StringBuilder();
			synchronized (getMonitor()) {
				doValidate();
				if (errorMessage.length() == 0) {
					doSave();
				}
			}
			if (errorMessage.length() == 0) {
				fireChange();
				refresh();
			} else {
				Modal.error(errorMessage.toString());
			}
		} catch (Exception e) {
			Modal.exception(e);
		}
	}

	protected void invokeDelete() {
		Modal.confirm(I18N.get("Delete"), event -> {
			try {
				synchronized (getMonitor()) {
					doDelete();
				}
				fireChange();
				fireClose();
			} catch (Exception e) {
				Modal.exception(e);
			}
		});
	}

	protected void invokeCopy() {
		try {
			copyObject();
			synchronized (getMonitor()) {
				doCopy();
			}
		} catch (Exception e) {
			Modal.exception(e);
		}
		disableButtons(true);
		saveButton.setDisabled(false);
		focus();
	}

	protected void copyObject() {
		try {
			setObject((E) getObject().getClass().newInstance());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	protected void disableButtons(boolean disabled) {
		saveButton.setDisabled(disabled);
		copyButton.setDisabled(disabled);
		deleteButton.setDisabled(disabled);
		loadButton.setDisabled(disabled);
	}

	public String getError() {
		return errorMessage.toString();
	}

	public void addError(String text) {
		errorMessage.append("- ").append(text).append("\n");
	}

	public void validate(boolean condition, String text) {
		if (condition) {
			addError(text);
		}
	}

	public void validate(String message) {
		validate(message != null, message);
	}

	public Component getButtons() {
		return buttons;
	}

	public Button getSaveButton() {
		return saveButton;
	}

	public Button getCopyButton() {
		return copyButton;
	}

	public Button getDeleteButton() {
		return deleteButton;
	}

	public Button getLoadButton() {
		return loadButton;
	}

	public void hideButtons() {
		saveButton.setVisible(false);
		copyButton.setVisible(false);
		deleteButton.setVisible(false);
		loadButton.setVisible(false);
	}

	protected void fireChange() {
		Events.sendEvent(new Event(Events.ON_CHANGE, this));
	}

	protected void fireClose() {
		Events.sendEvent(new Event(Events.ON_CLOSE, this));
	}

	public void exit() {
		fireClose();
	}

	public void setObject(E object) {
		this.object = object;
	}

	public E getObject() {
		return object;
	}

	protected void doInit() throws Exception {
		init();
	};

	protected void doLoad() throws Exception {
		load();
	};

	protected void doSave() throws Exception {
		save();
	};

	protected void doCopy() throws Exception {
		copy();
	};

	protected void doDelete() throws Exception {
		delete();
	};

	protected void doValidate() throws Exception {
		validate();
	};

	protected Object getMonitor() {
		return getObject() != null ? getObject() : this;
	}

	public String toString() {
		return String.valueOf(object);
	}
}
