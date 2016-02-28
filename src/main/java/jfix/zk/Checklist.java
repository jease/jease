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

import java.util.ArrayList;
import java.util.List;

import org.zkoss.zul.Box;
import org.zkoss.zul.Checkbox;

public class Checklist extends Box {

	private ItemRenderer itemRenderer;

	public void setItemRenderer(ItemRenderer renderer) {
		this.itemRenderer = renderer;
	}

	public Checklist orientVertical() {
		setOrient("vertical");
		return this;
	}

	public Checklist orientHorizontal() {
		setOrient("horizontal");
		return this;
	}

	public void clearSelection() {
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof ChecklistBox) {
				ChecklistBox checkBox = (ChecklistBox) getChildren().get(i);
				checkBox.setChecked(false);
			}
		}
	}

	public Object[] getChoices() {
		List<Object> result = new ArrayList<>();
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof ChecklistBox) {
				ChecklistBox checkBox = (ChecklistBox) getChildren().get(i);
				result.add(checkBox.getObject());
			}
		}
		return result.toArray();
	}

	public Object[] getSelected() {
		List<Object> result = new ArrayList<>();
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof ChecklistBox) {
				ChecklistBox checkBox = (ChecklistBox) getChildren().get(i);
				if (checkBox.isChecked()) {
					result.add(checkBox.getObject());
				}
			}
		}
		return result.toArray();

	}

	public void setChoices(List<Object> elements) {
		setChoices(elements.toArray());
	}

	public void setChoices(Object[] elements) {
		reset();
		if (elements != null) {
			for (int i = 0; i < elements.length; i++) {
				ChecklistBox cb = new ChecklistBox(elements[i]);
				if (itemRenderer != null) {
					cb.setLabel(itemRenderer.render(cb.getObject()));
				}
				appendChild(cb);
			}
		}
	}

	public void setSelected(List<Object> elements) {
		setSelected(elements.toArray());
	}

	public void setSelected(Object[] elements) {
		clearSelection();
		if (elements == null) {
			return;
		}

		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof ChecklistBox) {
				ChecklistBox checkBox = (ChecklistBox) getChildren().get(i);
				for (int j = 0; j < elements.length; j++) {
					if (checkBox.getObject().equals(elements[j])) {
						checkBox.setChecked(true);
						break;
					}
				}
			}
		}
	}

	public void setSelection(Object[] choices, Object[] elements) {
		setChoices(choices);
		setSelected(elements);
	}

	public void setSelection(List<Object> choices, Object[] elements) {
		setChoices(choices);
		setSelected(elements);
	}

	public void setSelection(Object[] choices, List<Object> elements) {
		setChoices(choices);
		setSelected(elements);
	}

	public void setSelection(List<Object> choices, List<Object> elements) {
		setChoices(choices);
		setSelected(elements);
	}

	public void reset() {
		getChildren().clear();
	}

	public boolean isEmpty() {
		if (getSelected() == null || getSelected().length == 0) {
			return true;
		}
		return false;
	}

	public void setDisabled(boolean flag) {
		for (int i = 0; i < getChildren().size(); i++) {
			if (getChildren().get(i) instanceof ChecklistBox) {
				ChecklistBox checkBox = (ChecklistBox) getChildren().get(i);
				checkBox.setDisabled(flag);
			}
		}
	}
}

class ChecklistBox extends Checkbox {

	private Object object;

	public ChecklistBox(Object object) {
		super(String.valueOf(object));
		setObject(object);
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}
}