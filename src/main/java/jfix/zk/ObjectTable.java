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

import java.util.List;

import jfix.util.I18N;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Box;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Separator;

public class ObjectTable<E> extends Div implements Refreshable {

	private Window editorWindow;
	private Component editor;
	private boolean modalEditor;
	private ObjectListbox<E> listbox;
	private ObjectTableModel<E> objectTableModel;
	private Hbox controlbox;
	private Hbox leftbox;
	private Hbox rightbox;
	private Button createButton;

	private Searchbox searchBox = new Searchbox() {
		public void searchPerformed(String search) {
			ObjectTable.this.searchPerformed(search);
		}
	};

	public ObjectTable() {
		setModalEditor(true);
		setWidth("100%");
	}

	public void init(ObjectTableModel<E> tablemodel) {
		init(tablemodel, null);
	}

	public void init(ObjectTableModel<E> tableModel, Component editor) {
		this.objectTableModel = tableModel;
		this.listbox = new ObjectListbox<E>(objectTableModel);

		createButton = new Button(I18N.get("New"), Images.DocumentNew);

		listbox.addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			public void onEvent(Event event) throws Exception {
				onSelect((E) listbox.getSelectedValue());
			}
		});

		createButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event event) throws Exception {
						onSelect(objectTableModel.newObject());
					}
				});

		if (editor == null || objectTableModel.newObject() == null) {
			createButton.setVisible(false);
		}

		setEditor(editor);
		rebuild();
		refresh();
	}

	public void rebuild() {
		getChildren().clear();

		leftbox = new Hbox();
		leftbox.setStyle("float:left;");
		leftbox.appendChild(createButton);

		rightbox = new Hbox();
		rightbox.setStyle("float:right;");
		rightbox.appendChild(searchBox);

		controlbox = new Hbox();
		controlbox.setWidth("100%");
		controlbox.setPack("stretch");
		controlbox.appendChild(leftbox);
		controlbox.appendChild(rightbox);

		appendChild(controlbox);
		appendChild(new Separator());
		appendChild(listbox);
	}

	public void setModalEditor(boolean modal) {
		this.modalEditor = modal;
	}

	public void hideControls() {
		controlbox.setVisible(false);
	}

	public void setSearch(String search) {
		searchBox.setSearch(search);
	}

	protected void searchPerformed(String search) {
		objectTableModel.setSearch(search);
		listbox.clearSelection();
		if (listbox.getMold().equals("paging")) {
			listbox.setActivePage(0);
		}
	}

	private void showEditor(Object object) {
		if (getEditor() != null && object != null) {

			if (getEditor() instanceof Objectable) {
				((Objectable<Object>) getEditor()).setObject(object);
			}

			if (getEditor() instanceof Refreshable) {
				((Refreshable) getEditor()).refresh();
			}

			getEditorWindow().setTitle(getEditor());
			getEditorWindow().setParent(getRoot());

			if (modalEditor) {
				getEditorWindow().doModal();
			}
		}
	}

	public void refresh() {
		listbox.refresh();
	}

	public List<E> getElements() {
		return objectTableModel.getElements();
	}

	public Window getEditorWindow() {
		if (editorWindow == null) {
			editorWindow = new Window();
			editorWindow.addEventListener(Events.ON_CLOSE,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							if (editor != null) {
								Events.sendEvent(editor, event);
							}
							if (event.isPropagatable()) {
								editorWindow.close();
								clearSelection();
								refresh();
							}
						}
					});
		}
		return editorWindow;
	}

	public Box getLeftbox() {
		return leftbox;
	}

	public Box getRightbox() {
		return rightbox;
	}

	public Button getCreateButton() {
		return createButton;
	}

	public Listbox getListbox() {
		return listbox;
	}

	public void clearSelection() {
		listbox.clearSelection();
	}

	public Component getEditor() {
		return editor;
	}

	public void setEditor(Component ed) {
		editor = ed;
		if (editor != null) {
			if (!getEditorWindow().getChildren().contains(editor)) {
				getEditorWindow().getChildren().clear();
				getEditorWindow().appendChild(editor);
			}
			editor.addEventListener(Events.ON_CLOSE,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							getEditorWindow().close();
							clearSelection();
							refresh();
						}
					});
			editor.addEventListener(Events.ON_CHANGE,
					new EventListener<Event>() {
						public void onEvent(Event event) {
							getEditorWindow().setTitle(getEditor());
							refresh();
						}
					});
		}
	}

	protected void onSelect(Object object) {
		showEditor(object);
	}
}
