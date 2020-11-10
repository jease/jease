/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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
package jease.cmf.web.node;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.i18n.Strings;
import jfix.zk.ActionListener;
import jfix.zk.Checkbox;
import jfix.zk.Image;
import jfix.zk.Label;
import jfix.zk.Modal;
import jfix.zk.Row;
import jfix.zk.View;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.event.Event;

public class NodeViews {

	public static View asType(Node node) {
		return new View(node.getType(), new Row(new Image(JeaseSession
				.getConfig().getIcon(node)), new Label(node.getType())));
	}
	
	public static View asIcon(Node node) {
		Image image = new Image();
		image.setSrc(JeaseSession.getConfig().getIcon(node));
		image.setTooltiptext(node.getType());
		return new View(node.getType(), image);
	}

	public static View asCheckbox(boolean state, final ActionListener action) {
		final Checkbox checkbox = new Checkbox();
		checkbox.setChecked(state);
		checkbox.addCheckListener(new ActionListener() {
			public void actionPerformed(Event evt) {
				Modal.click(checkbox, evt, action);
			}
		});
		return new View(state ? Strings.Yes : Strings.No, checkbox);
	}

	public static View asSize(Node node) {
		if (node.isContainer()) {
			long items = node.getChildren().length;
			String text = String.format(" %s %s", items,
					items == 1 ? Strings.Item : Strings.Items);
			return new View(text, new Label(text));
		} else {
			long size = node.getSize();
			String text = FileUtils.byteCountToDisplaySize(size);
			return new View(size, new Label(text));
		}
	}
}
