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
package jease.cmf.web.node;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseSession;
import jfix.util.I18N;
import jfix.zk.Row;
import jfix.zk.View;

import org.apache.commons.io.FileUtils;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Image;
import org.zkoss.zul.Label;

public class NodeViews {

	public static View<String> asType(Node node) {
		return new View<String>(node.getType(), new Row(new Image(JeaseSession
				.getConfig().getIcon(node)), new Label(node.getType())));
	}

	public static View<String> asIcon(Node node) {
		Image image = new Image();
		image.setStyle("margin-left: 5px;");
		image.setSrc(JeaseSession.getConfig().getIcon(node));
		image.setTooltiptext(node.getType());
		return new View<String>(node.getType(), image);
	}

	public static View<String> asCheckbox(boolean state,
			final EventListener<Event> action) {
		final Checkbox checkbox = new Checkbox();
		checkbox.setChecked(state);
		checkbox.addEventListener(Events.ON_CHECK, action);
		return new View<String>(state ? I18N.get("Yes") : I18N.get("No"),
				checkbox);
	}

	public static View<String> asSize(Node node) {
		if (node.isContainer()) {
			long items = node.getChildren().length;
			String text = String.format(" %s %s", items,
					items == 1 ? I18N.get("Item") : I18N.get("Items"));
			return new View<String>(text, new Label(text));
		} else {
			long size = node.getSize();
			String text = FileUtils.byteCountToDisplaySize(size);
			return new View<String>(String.valueOf(size), new Label(text));
		}
	}
}
