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

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;

public class Tabbox<E> extends org.zkoss.zul.Tabbox implements Refreshable,
		Objectable<E> {

	private Tabs tabs = new Tabs();
	private Tabpanels tabpanels = new Tabpanels();

	public Tabbox() {
		appendChild(tabs);
		appendChild(tabpanels);
		addEventListener(Events.ON_SELECT, new EventListener<Event>() {
			public void onEvent(Event evt) {
				refresh();
			}
		});
	}

	public void add(String title, Component comp) {
		add(title, comp, null);
	}

	public void add(String title, Component comp, String image) {
		RefreshableTabpanel tabpanel = new RefreshableTabpanel();
		tabpanel.appendChild(comp);
		tabs.appendChild(new Tab(title, image));
		tabpanels.appendChild(tabpanel);
	}

	public void add(String title, Class<?> clazz) {
		add(title, clazz, null);
	}

	public void add(String title, Class<?> clazz, String image) {
		RefreshableTabpanel tabpanel = new RefreshableTabpanel(clazz);
		if (tabpanels.getChildren().size() == 0) {
			tabpanel.refresh();
		}
		tabs.appendChild(new Tab(title, image));
		tabpanels.appendChild(tabpanel);
	}

	public void reset() {
		tabs.getChildren().clear();
		tabpanels.getChildren().clear();
	}

	public void refresh() {
		if (getSelectedPanel() instanceof Refreshable) {
			((Refreshable) getSelectedPanel()).refresh();
		}
	}

	public void setObject(E object) {
		for (Object cmp : tabpanels.getChildren()) {
			if (cmp instanceof Objectable) {
				((Objectable<Object>) cmp).setObject(object);
			}
		}
	}

	public E getObject() {
		return null;
	}
}

class RefreshableTabpanel extends Tabpanel implements Refreshable,
		Objectable<Object> {

	private Class<?> componentClass;

	public RefreshableTabpanel() {
	}

	public RefreshableTabpanel(Class<?> componentClass) {
		this.componentClass = componentClass;
	}

	public void refresh() {
		if (componentClass != null) {
			try {
				appendChild((Component) componentClass.newInstance());
				componentClass = null;
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
		}

		for (Object child : getChildren()) {
			if (child instanceof Refreshable) {
				((Refreshable) child).refresh();
			}
		}
	}

	public void setObject(Object object) {
		if (getFirstChild() instanceof Objectable) {
			((Objectable<Object>) getFirstChild()).setObject(object);
		}
	}
}
