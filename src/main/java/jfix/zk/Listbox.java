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

import org.zkoss.zul.ListModel;
import org.zkoss.zul.SimpleListModel;
import org.zkoss.zul.ext.Selectable;

public class Listbox extends org.zkoss.zul.Listbox {

	public Listbox() {
		setSeltype("single");
	}

	public Listbox(List<Object> values) {
		setValues(values);
	}

	public Listbox(Object[] values) {
		setValues(values);
	}

	public Listbox doPaged(int pageSize) {
		setMold("paging");
		setPageSize(pageSize);
		return this;
	}

	public void setValues(Object[] values) {
		if (values != null) {
			setModel(new SimpleListModel<Object>(values));
		}
	}

	public void setValues(List<?> values) {
		if (values != null) {
			setModel(new SimpleListModel<Object>(values));
		}
	}

	public void setModel(ListModel<?> model) {
		super.setModel(model);
	}

	public void setSelection(List<?> values, Object selected) {
		setValues(values);
		setSelectedValue(selected);
	}

	public void setModel(Object[] values, Object selected) {
		setValues(values);
		setSelectedValue(selected);
	}

	public Object getSelectedValue() {
		if (getSelectedIndex() != -1) {
			return getModel().getElementAt(getSelectedIndex());
		}
		return null;
	}

	public Object[] getValues() {
		List<Object> values = new ArrayList<>();
		for (int i = 0; i < getItemCount(); i++) {
			Object value = getItemAtIndex(i).getValue();
			if (value != null) {
				values.add(getItemAtIndex(i).getValue());
			}
		}
		return values.toArray(new Object[] {});
	}

	public void setSelectedValue(Object value) {
		setSelectedIndex(-1);
		if (value != null) {
			for (int i = 0; i < getModel().getSize(); i++) {
				if (value.equals(getModel().getElementAt(i))) {
					setSelectedIndex(i);
					return;
				}
			}
		}
	}

	public boolean isEmpty() {
		return getSelectedValue() == null;
	}

	public void clearSelection() {
		super.clearSelection();
		((Selectable<Object>) getModel()).clearSelection();
	}

}
