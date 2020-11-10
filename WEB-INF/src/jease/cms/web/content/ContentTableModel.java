/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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

import jease.cmf.service.Nodes;
import jease.cmf.web.node.NodeTableModel;
import jease.cmf.web.node.NodeViews;
import jease.cms.domain.Content;
import jease.cms.web.i18n.Strings;
import jfix.zk.ActionListener;

import org.zkoss.zk.ui.event.CheckEvent;
import org.zkoss.zk.ui.event.Event;

public class ContentTableModel extends NodeTableModel<Content> {

	public String[] getColumns() {
		return new String[] { Strings.Type, Strings.Id, Strings.Title,
				Strings.Last_modified, Strings.Editor, Strings.Size,
				Strings.Visible };
	}

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

	public Object[] getSearchValues(Content content) {
		return new Object[] { content.getFulltext().toString().replaceAll("\\<.*?\\>", "") };
	}

}
