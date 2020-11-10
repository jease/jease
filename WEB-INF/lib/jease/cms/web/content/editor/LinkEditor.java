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
package jease.cms.web.content.editor;

import jease.cms.domain.Link;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Images;
import jfix.zk.Linkbutton;
import jfix.zk.Row;
import jfix.zk.Textfield;
import jfix.zk.WebBrowser;

import org.zkoss.zk.ui.event.Event;

public class LinkEditor extends ContentEditor<Link> {

	Textfield url = new Textfield();
	Linkbutton link = new Linkbutton();

	public LinkEditor() {
		link.setTooltiptext(I18N.get("Open"));
		link.setImage(Images.InternetWebBrowser);
		link.setWidth("24px");
		link.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				getRoot().appendChild(new WebBrowser(url.getText()));
			}
		});
	}

	public void init() {
		add(I18N.get("Url"), new Row(url, link));
	}

	public void load() {
		url.setText(getNode().getUrl());
	}

	public void save() {
		getNode().setUrl(url.getText());
	}

	public void validate() {
		validate(url.isEmpty(), I18N.get("Url_is_required"));
	}
}
