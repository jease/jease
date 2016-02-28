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
import java.util.Comparator;
import java.util.List;

import jfix.search.FullTextIndex;

import org.zkoss.zul.AbstractListModel;
import org.zkoss.zul.event.ListDataEvent;
import org.zkoss.zul.ext.Sortable;

public abstract class ObjectTableModel<E> extends AbstractListModel<E>
		implements Sortable<E>, Refreshable {

	private List<E> allElements;
	private List<E> filteredElements;
	private String search;
	private FullTextIndex<E> fullTextIndex;
	private Comparator<E> comparator;

	public abstract E newObject();

	public abstract String[] getColumns();

	public abstract Object getValue(E object, int column);

	public abstract List<E> getList();

	public boolean isSortable() {
		return true;
	}

	public int[] getProportions() {
		int[] props = new int[getColumns().length];
		for (int i = 0; i < props.length; i++) {
			props[i] = 1;
		}
		return props;
	}

	public Object[] getSearchValues(E object) {
		return null;
	}

	public ObjectTableModel() {
		allElements = new ArrayList<E>();
		filteredElements = new ArrayList<E>();
	}

	public List<E> getElements() {
		return filteredElements;
	}

	public E getElementAt(int i) {
		if (i < filteredElements.size()) {
			return filteredElements.get(i);
		} else {
			return null;
		}
	}

	public int getSize() {
		return filteredElements.size();
	}

	public E getCheckedValue(E object, int column) {
		if (object == null) {
			return null;
		}
		try {
			return (E) getValue(object, column);
		} catch (NullPointerException e) {
			return null;
		}
	}

	public void refresh() {
		fullTextIndex = null;
		allElements = getList();
		setSearch(search);
	}

	public void setSearch(String searchStr) {
		try {
			search = searchStr;
			if (search != null && !"".equals(search.trim())) {
				if (fullTextIndex == null) {
					buildFullTextIndex(allElements);
				}
				filteredElements = new ArrayList<E>();
				for (E entity : fullTextIndex.search(search)) {
					filteredElements.add(entity);
				}
			} else {
				if (allElements != null) {
					filteredElements = new ArrayList<E>(allElements);
				} else {
					filteredElements = new ArrayList<E>();
				}
			}
			sort(comparator, true);
		} catch (Exception e) {
			search = null;
			Modal.error(e.getLocalizedMessage());
		}
	}

	public String getSearch() {
		return search;
	}

	private void buildFullTextIndex(List<E> elements) {
		fullTextIndex = new FullTextIndex<E>();
		int columnCount = getColumns().length;
		for (E entity : elements) {
			StringBuilder fulltext = new StringBuilder(256);
			Object[] searchValues = getSearchValues(entity);
			if (searchValues != null) {
				fulltext.append(ObjectConverter.convert(searchValues));
				fulltext.append("\t");
			}
			for (int column = 0; column < columnCount; column++) {
				fulltext.append(ObjectConverter.convert(getCheckedValue(entity,
						column)));
				fulltext.append("\t");
			}
			fullTextIndex.add(entity, fulltext.toString());
		}
		fullTextIndex.commit();
	}

	public void sort(Comparator<E> cmp, boolean ascending) {
		if (cmp != null) {
			this.comparator = cmp;
		}
		if (this.comparator != null) {
			filteredElements.sort(comparator);
		}
		fireEvent(ListDataEvent.CONTENTS_CHANGED, -1, -1);
	}

	public String getSortDirection(Comparator<E> comparator) {
		return null;
	}
}
