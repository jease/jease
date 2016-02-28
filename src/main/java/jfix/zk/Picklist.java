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

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import jfix.search.FullTextIndex;
import jfix.util.I18N;
import jfix.util.Natural;

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModelList;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.ListitemRenderer;
import org.zkoss.zul.Separator;

public class Picklist extends Column {

	private int rows;

	private ListModelList<Object> choiceModel = new ListModelList<>();
	private ListModelList<Object> selectionModel = new ListModelList<>();
	private ListModelList<Object> backupModel;

	private Listbox choiceListbox = new Listbox();
	private Listbox selectionListbox = new Listbox();

	private Comparator<Object> choiceComparator;
	private Comparator<Object> selectionComparator;

	private Searchbox searchBox = new Searchbox() {
		public void searchPerformed(String search) {
			try {
				Picklist.this.searchPerformed(search);
			} catch (Exception e) {
				Modal.error(e.getMessage());
			}
		}
	};

	public Picklist() {
		this(Natural.newComparator(), null);
	}

	public Picklist(boolean open) {
		this(Natural.newComparator(), Natural.newComparator(), open);
	}

	public Picklist(Comparator<Object> comparator) {
		this(comparator, comparator);
	}

	public Picklist(Comparator<Object> comparator, boolean open) {
		this(comparator, comparator, open);
	}

	public Picklist(Comparator<Object> choiceListComparator,
			Comparator<Object> selectionListComparator) {
		this(choiceListComparator, selectionListComparator, false);
	}

	public Picklist(Comparator<Object> choiceListComparator,
			Comparator<Object> selectionListComparator, boolean open) {
		this.choiceComparator = choiceListComparator;
		this.selectionComparator = selectionListComparator;

		Button selectButton = new Button("", Images.GoUp);
		selectButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						actionSelect();
					}
				});

		Button deselectButton = new Button("", Images.GoDown);
		deselectButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						actionDeselect();
					}
				});

		Button upButton = new Button("", Images.GoUp);
		upButton.addEventListener(Events.ON_CLICK, new EventListener<Event>() {
			public void onEvent(Event e) {
				actionMoveUp();
			}
		});

		Button downButton = new Button("", Images.GoDown);
		downButton.addEventListener(Events.ON_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event e) {
						actionMoveDown();
					}
				});

		selectionListbox.setOddRowSclass("null");
		selectionListbox.setHflex("1");
		selectionListbox.setMultiple(true);
		selectionListbox.setModel(selectionModel);
		selectionListbox.addEventListener(Events.ON_DOUBLE_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event evt) throws Exception {
						actionDeselect();
					}
				});

		choiceListbox.setOddRowSclass("null");
		choiceListbox.setHflex("1");
		choiceListbox.setMultiple(true);
		choiceListbox.setModel(choiceModel);
		choiceListbox.addEventListener(Events.ON_DOUBLE_CLICK,
				new EventListener<Event>() {
					public void onEvent(Event evt) throws Exception {
						actionSelect();
					}
				});

		setRows(5);

		Row selectionControls = new Row();
		selectionControls.appendChild(selectionListbox);
		if (selectionListComparator == null) {
			selectionControls.appendChild(new Div(upButton, new Separator(
					"horizontal"), downButton));
		}

		appendChild(selectionControls);
		if (open) {
			appendChild(new Column(new Row(selectButton, deselectButton,
					searchBox), choiceListbox));
		} else {
			appendChild(new Expander(I18N.get("Selection"), new Column(new Row(
					selectButton, deselectButton, searchBox), choiceListbox),
					open));
		}
	}

	public void setMaxlength(int cols) {
		choiceListbox.setMaxlength(cols);
		selectionListbox.setMaxlength(cols);
	}

	public void setRows(int numrows) {
		rows = numrows;
		selectionListbox.setRows(rows);
		choiceListbox.setRows(rows);
	}

	public void setItemRenderer(ListitemRenderer<Object> renderer) {
		choiceListbox.setItemRenderer(renderer);
		selectionListbox.setItemRenderer(renderer);
	}

	public void setSearchable(boolean searchable) {
		searchBox.setVisible(searchable);
	}

	public void clearSelection() {
		choiceListbox.clearSelection();
		selectionListbox.clearSelection();
		if (choiceModel.isEmpty()) {
			searchBox.clearSearch();
			try {
				searchPerformed("");
			} catch (Exception e) {
				// pass
			}
		}
	}

	public void setChoices(List<Object> elements) {
		setChoices(elements.toArray());
	}

	public void setChoices(Object elements[]) {
		backupModel = null;
		searchBox.clearSearch();
		clearSelection();
		choiceModel.clear();
		removeFromSelected(elements);
	}

	public void setSelected(List<Object> elements) {
		setSelected(elements.toArray());
	}

	public void setSelected(Object elements[]) {
		backupModel = null;
		clearSelection();
		selectionModel.clear();
		addToSelected(elements);
	}

	public void setSelection(Object[] choices, Object[] selected) {
		setChoices(choices);
		setSelected(selected);
	}

	public void setSelection(List<Object> choices, Object[] selected) {
		setChoices(choices);
		setSelected(selected);
	}

	public void setSelection(Object[] choices, List<Object> selected) {
		setChoices(choices);
		setSelected(selected);
	}

	public void setSelection(List<Object> choices, List<Object> selected) {
		setChoices(choices);
		setSelected(selected);
	}

	public void addToSelected(Object elements[]) {
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				if (elements[i] != null) {
					choiceModel.remove(elements[i]);
					selectionModel.add(elements[i]);
				}
			}
			if (choiceComparator != null) {
				sortModels(choiceModel, choiceComparator);
			}

			if (selectionComparator != null) {
				sortModels(selectionModel, selectionComparator);
			}
		}
		adjustRows();
	}

	public void removeFromSelected(Object elements[]) {
		if (elements != null) {
			for (int i = elements.length - 1; i >= 0; i--) {
				if (elements[i] != null) {
					selectionModel.remove(elements[i]);
					choiceModel.add(elements[i]);
				}
			}
			if (choiceComparator != null) {
				sortModels(choiceModel, choiceComparator);
			}

			if (selectionComparator != null) {
				sortModels(selectionModel, selectionComparator);
			}
		}
		adjustRows();
	}

	private void adjustRows() {
		if (isEmpty()) {
			selectionListbox.setRows(1);
		} else {
			int len = getSelected().length;
			selectionListbox.setRows(len < rows ? len : rows);
		}
	}

	private void sortModels(ListModelList<Object> model,
			Comparator<Object> comparator) {
		model.sort(comparator, true);
	}

	private void moveUp(Object elements[]) {
		if (elements != null) {
			int[] selectedIndices = new int[elements.length];
			for (int i = 0; i < elements.length; i++) {
				int index = selectionModel.indexOf(elements[i]);
				if (index > 0) {
					selectionModel.remove(index);
					selectedIndices[i] = index - 1;
					selectionModel.add(selectedIndices[i], elements[i]);
				} else {
					selectedIndices[i] = 0;
				}
			}
			for (int i : selectedIndices) {
				selectionListbox.setSelectedIndex(i);
			}
		}
	}

	private void moveDown(Object elements[]) {
		if (elements != null) {
			int[] selectedIndices = new int[elements.length];
			for (int i = elements.length - 1; i >= 0; i--) {
				int index = selectionModel.indexOf(elements[i]);
				if (index < selectionModel.size() - 1) {
					selectionModel.remove(index);
					selectedIndices[i] = index + 1;
					selectionModel.add(selectedIndices[i], elements[i]);
				} else {
					selectedIndices[i] = selectionModel.size() - 1;
				}
			}
			for (int i : selectedIndices) {
				selectionListbox.setSelectedIndex(i);
			}
		}
	}

	public Object[] getChoices() {
		return convertListModelToArray(choiceModel);
	}

	public Object[] getSelected() {
		return convertListModelToArray(selectionModel);
	}

	private void actionSelect() {
		addToSelected(convertItemSetToArray(choiceListbox.getSelectedItems()));
		clearSelection();
	}

	private void actionDeselect() {
		removeFromSelected(convertItemSetToArray(selectionListbox
				.getSelectedItems()));
		clearSelection();
	}

	private void actionMoveUp() {
		moveUp(convertItemSetToArray(selectionListbox.getSelectedItems()));
		choiceListbox.clearSelection();
	}

	private void actionMoveDown() {
		moveDown(convertItemSetToArray(selectionListbox.getSelectedItems()));
		choiceListbox.clearSelection();
	}

	private Object[] convertItemSetToArray(Set<Listitem> listitems) {
		int i = 0;
		Object[] values = new Object[listitems.size()];
		for (Object item : listitems) {
			values[i++] = ((Listitem) item).getValue();
		}
		return values;
	}

	private Object[] convertListModelToArray(ListModelList<Object> model) {
		Object[] arr = new Object[model.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = model.get(i);
		}
		return arr;
	}

	public boolean isEmpty() {
		if (getSelected() == null || getSelected().length == 0) {
			return true;
		}
		return false;
	}

	private void searchPerformed(String searchTerm) throws Exception {
		if (backupModel == null) {
			backupModel = new ListModelList<Object>();
			for (int i = 0; i < choiceModel.size(); i++) {
				backupModel.add(choiceModel.get(i));
			}
			for (int i = 0; i < selectionModel.size(); i++) {
				backupModel.add(selectionModel.get(i));
			}
		}
		choiceModel.clear();
		if (searchTerm == null || "".equals(searchTerm)) {
			for (int i = 0; i < backupModel.size(); i++) {
				Object entity = backupModel.get(i);
				if (selectionModel.indexOf(entity) == -1) {
					choiceModel.add(entity);
				}
			}
		} else {
			FullTextIndex<Object> index = new FullTextIndex<>();
			for (int i = 0; i < backupModel.size(); i++) {
				Object entity = backupModel.get(i);
				if ((choiceListbox.getItemRenderer()) instanceof ItemRenderer) {
					index.add(entity, ((ItemRenderer) choiceListbox
							.getItemRenderer()).render(entity));
				} else {
					index.add(entity, String.valueOf(entity));
				}
			}
			index.commit();
			String normalizedSearchTerm = normalizeSearch(searchTerm);
			for (Object obj : index.search(normalizedSearchTerm)) {
				if (selectionModel.indexOf(obj) == -1) {
					choiceModel.add(obj);
				}
			}
			choiceListbox.selectAll();
		}
	}

	public Combobox getSearchField() {
		return searchBox.getSearchField();
	}

	protected String normalizeSearch(String searchTerm) {
		return searchTerm;
	}
}
