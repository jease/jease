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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.ListModel;
import org.zkoss.zul.ListitemRenderer;

public class Selectbutton extends Div {

	private Selectfield selectfield = new Selectfield();
	private Button button = new Button();

	public Selectbutton() {
		selectfield.setHflex("1");
		button.setHflex("1");
		button.addEventListener(Events.ON_CLICK, event -> showSelection(null));
		appendChild(button);
	}

	private void showSelection(Object value) {
		button.setParent(null);
		selectfield.setParent(null);
		if (getItemRenderer() instanceof ItemRenderer) {
			button.setLabel(((ItemRenderer) getItemRenderer()).render(value));
		} else {
			button.setLabel(String.valueOf(value));
		}
		if (value == null) {
			appendChild(selectfield);
		} else {
			appendChild(button);
		}
	}

	public Object getSelectedValue() {
		return selectfield.getSelectedValue();
	}

	public boolean isEmpty() {
		return selectfield.isEmpty();
	}

	public void setSelection(List<Object> values, Object value) {
		selectfield.setSelection(values, value);
		showSelection(value);
	}

	public void setSelectedValue(Object value) {
		selectfield.setSelectedValue(value);
		showSelection(value);
	}

	public void setValues(Object[] values) {
		selectfield.setValues(values);
		showSelection(getSelectedValue());
	}

	public void setValues(List<Object> values) {
		selectfield.setValues(values);
		showSelection(getSelectedValue());
	}

	public void setModel(ListModel<Object> model) {
		selectfield.setModel(model);
		showSelection(getSelectedValue());
	}

	public void setModel(Object[] values, Object selected) {
		selectfield.setModel(values, selected);
		showSelection(selected);
	}

	public Object[] getValues() {
		return selectfield.getValues();
	}

	public void addSelectListener(final EventListener<Event> eventListener) {
		selectfield.addEventListener(Events.ON_SELECT, eventListener);
	}

	public void setDisabled(boolean disabled) {
		selectfield.setDisabled(disabled);
		button.setDisabled(disabled);
	}

	public void setItemRenderer(ListitemRenderer<Object> renderer) {
		selectfield.setItemRenderer(renderer);
	}

	public ListitemRenderer<Object> getItemRenderer() {
		return selectfield.getItemRenderer();
	}
}
