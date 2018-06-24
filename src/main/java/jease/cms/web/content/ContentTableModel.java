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
package jease.cms.web.content;

import java.util.Arrays;
import java.util.Set;

import jease.Names;
import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cmf.web.node.NodeTableModel;
import jease.cmf.web.node.NodeViews;
import jease.cmf.web.node.tree.container.ContainerTable;
import jease.cms.domain.Content;
import jfix.util.I18N;
import jfix.zk.Listbox;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;

public class ContentTableModel extends NodeTableModel<Node> {

	/**
	 * Which column names should be displayed as headers?
	 */
	public String[] getColumns() {
		String[] columns = new String[] { I18N.get("Type"), I18N.get("Id"),
				I18N.get("Title"), I18N.get("Last_modified"),
				I18N.get("Editor"), I18N.get("Size"), I18N.get("Visible") };
		if (Registry.getParameter(Names.JEASE_SITE_DESIGN) != null) {
			return columns;
		} else {
			return Arrays.copyOf(columns, columns.length - 1);
		}
	}

	/**
	 * Which relative width (proportion) should be used to size a column?
	 */
	public int[] getProportions() {
		return new int[] { 2, 5, 8, 5, 4, 3, 2 };
	}

	/**
	 * Which value should be displayed in a given column?
	 */
	public Object getValue(Node node, int column) {
		final Content content = (Content) node;
		switch (column) {
		case 0:
			return NodeViews.asIcon(content);
		case 1:
			return content.getId();
		case 2:
			return content.getTitle();
		case 3:
			return content.getLastModified();
		case 4:
			return content.getEditor();
		case 5:
			return NodeViews.asSize(content);
		case 6:
			return NodeViews.asCheckbox(
					content.isVisible(),
					event -> visibilityChangePerformed(content,
							(Checkbox) event.getTarget()));
		}
		return "";
	}

	/**
	 * Which content should be searchable in addition to columns above?
	 */
	public Object[] getSearchValues(Node content) {
		return new Object[] { ((Content) content).getFulltext() };
	}

	private void visibilityChangePerformed(Content content, Checkbox checkbox) {
		// Change visibility for actual selected item
		content.setVisible(checkbox.isChecked());
		Nodes.save(content);

		// Change visibility for multiple selected items
		Listcell listcell = (Listcell) checkbox.getParent();
		Listbox listbox = (Listbox) listcell.getListbox();
		Set<Listitem> selectedItems = listbox.getSelectedItems();
		if (!selectedItems.isEmpty()) {
			for (Listitem item : listbox.getSelectedItems()) {
				Content node = item.getValue();
				node.setVisible(checkbox.isChecked());
				Nodes.save(node);
			}
			listbox.clearSelection();
			((ContainerTable) listbox.getParent()).refresh();
		}
	}
}
