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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.TreeModel;

public class Tree extends org.zkoss.zul.Tree implements Refreshable {

	public Tree() {
	}

	public void fireChangeEvent() {
		Events.sendEvent(new Event(Events.ON_CHANGE, this));
	}

	public void refresh() {
		TreeModel<?> model = (TreeModel<?>) getModel();
		setModel(null);
		setModel(model);
	}

	public Object getSelectedValue() {
		return getSelectedItem() != null ? getSelectedItem().getValue() : null;
	}

	public void resetSelectedValue() {
		setSelectedItem(null);
	}

}
