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

import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Button;
import org.zkoss.zul.Hbox;

public abstract class Searchbox extends Hbox {

	Combobox searchField = new Combobox();
	Button searchButton = new Button(I18N.get("Search"), Images.SystemSearch);
	Button resetButton = new Button(I18N.get("Reset"), Images.ViewRefresh);

	public abstract void searchPerformed(String search);

	public Searchbox() {
		appendChild(searchField);
		appendChild(searchButton);
		appendChild(resetButton);
		initListener();
	}

	private void initListener() {
		searchField.setButtonVisible(false);

		EventListener<Event> searchAction = $event -> searchPerformed(searchField
				.getText());

		EventListener<Event> resetAction = $event -> {
			clearSearch();
			searchPerformed(null);
		};

		searchButton.addEventListener(Events.ON_CLICK, searchAction);
		resetButton.addEventListener(Events.ON_CLICK, resetAction);
		searchField.addEventListener(Events.ON_OK, searchAction);
		searchField.addEventListener(Events.ON_SELECT, searchAction);
	}

	public void setSearch(String search) {
		searchField.setText(search);
		searchPerformed(search);
	}

	public void clearSearch() {
		searchField.setText(null);
	}

	public Combobox getSearchField() {
		return searchField;
	}
}
