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

import java.net.URI;

import jease.cmf.service.Filenames;
import jease.cms.domain.Transit;
import jfix.util.I18N;
import jfix.util.Validations;
import jfix.zk.ActionListener;
import jfix.zk.Checkbox;
import jfix.zk.Combobox;
import jfix.zk.Mediafield;
import jfix.zk.ZK;

import org.zkoss.zk.ui.event.Event;

public class TransitEditor extends ContentEditor<Transit> {

	Combobox uri = new Combobox();
	Mediafield file = new Mediafield();
	Checkbox forward = new Checkbox();

	public TransitEditor() {
		file.setHeight((100 + getDesktopHeight() / 3) + "px");
		uri.addSelectListener(new ActionListener() {
			public void actionPerformed(Event paramEvent) {
				pathSelected();
			}
		});
	}

	public void init() {
		add(I18N.get("Path"), uri);
		add(I18N.get("File"), file);
		add(I18N.get("Forward"), forward);
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
		forward.setChecked(getNode().isForward());
	}

	public void save() {
		getNode().setURI((String) uri.getValue());
		file.copyToFile(getNode().getFile());
		getNode().setForward(forward.isChecked());
	}

	public void validate() {
		validate(uri.isEmpty(),I18N.get("Path_is_required"));
		validate(!URI.create(uri.getText()).isAbsolute(),
				I18N.get("Path_is_required"));
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
