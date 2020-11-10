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
package jease.cms.web.content.editor;

import java.net.URI;

import jease.cmf.service.Filenames;
import jease.cms.domain.Transit;
import jease.cms.web.i18n.Strings;
import jfix.util.Validations;
import jfix.zk.ActionListener;
import jfix.zk.Combobox;
import jfix.zk.Mediafield;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Event;

public class TransitEditor extends ContentEditor<Transit> {

	Combobox uri = new Combobox();
	Mediafield file = new Mediafield();

	public TransitEditor() {
		file.setHeight("350px");
		uri.addSelectListener(new ActionListener() {
			public void actionPerformed(Event paramEvent) {
				pathSelected();
			}
		});
	}

	public void init() {
		add(Strings.Path, uri);
		add(Strings.File, file);
	}

	public void load() {
		uri.setSelection(Filenames.getPathnames(ZK.getRealPath("/")),
				getObject().getURI());
		if (getNode().getURI() != null) {
			uri.setDisabled(true);
			file.setMedia(getNode().getId(), getNode().getContentType(),
					getNode().getFile());
		} else {
			uri.setDisabled(false);
		}
	}

	public void save() {
		getNode().setURI((String) uri.getValue());
		file.copyToFile(getNode().getFile());
	}

	public void validate() {
		validate(uri.isEmpty(), Strings.Path_is_required);
		validate(!URI.create(uri.getText()).isAbsolute(),
				Strings.Path_is_required);
	}

	private void pathSelected() {
		String pathname = (String) uri.getValue();
		if (Validations.isNotEmpty(pathname)) {
			String name = Filenames.asFilename(pathname);
			if (id.isEmpty()) {
				id.setText(Filenames.asId(name));
			}
			if (title.isEmpty()) {
				title.setText(Filenames.asTitle(name));
			}
		}
	}

}
