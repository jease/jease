/*
    Copyright (C) 2011 maik.jablonski@jease.org

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

import jease.Names;
import jease.Registry;
import jease.cmf.service.Nodes;
import jease.cmf.web.node.NodeTableModel;
import jease.cmf.web.node.NodeViews;
import jease.cms.domain.Content;
import jfix.util.I18N;
import jfix.zk.ActionListener;

import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;

public class ContentTableModel extends NodeTableModel<Content> {

	/**
	 * Which column names should be displayed as headers?
	 */
	public String[] getColumns() {
		String[] columns = new String[] { I18N.get("Type"), I18N.get("Id"),
				I18N.get("Title"), I18N.get("Last_modified"), I18N.get("Editor"),
				I18N.get("Size"), I18N.get("Visible") };
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
		return new int[] { 2, 8, 8, 5, 4, 3, 2 };
	}

	/**
	 * Which value should be displayed in a given column?
	 */
	public Object getValue(final Content content, int column) {
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
			return NodeViews.asCheckbox(content.isVisible(),
					new ActionListener() {
						public void actionPerformed(Event event) {
							boolean visible = ((CheckEvent) event).isChecked();
							content.setVisible(visible);
							Nodes.save(content);
						}
					});
		}
		return "";
	}

	/**
	 * Which content should be searchable in addition to columns above?
	 */
	public Object[] getSearchValues(Content content) {
		return new Object[] { content.getFulltext() };
	}
}
