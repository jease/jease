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
package org.jease.cms.web.content.editor;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jease.cms.domain.Transit;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;

import org.jease.cms.domain.Transit;
import jfix.util.I18N;
import jfix.zk.Combobox;
import jfix.zk.Mediafield;
import jfix.zk.ZK;

public class TransitEditor extends ContentEditor<Transit> {

	Combobox uri = new Combobox();
	Mediafield file = new Mediafield();
	Checkbox forward = new Checkbox();

	public TransitEditor() {
		file.setHeight(getPlainEditorHeight());
		uri.addEventListener(Events.ON_SELECT, event -> pathSelected());
	}

	@Override
    public void init() {
		add(I18N.get("Path"), uri);
		add(I18N.get("File"), file);
		add(I18N.get("Forward"), forward);
	}

	@Override
    public void load() {
		uri.setSelection(getPathnames(ZK.getRealPath("/")), getObject()
				.getURI());
		if (getNode().getURI() != null) {
			file.setMedia(getNode().getId(), getNode().getContentType(),
					getNode().getFile());
		}
		forward.setChecked(getNode().isForward());
	}

	@Override
    public void save() {
		getNode().setURI(uri.getValue());
		getNode().setForward(forward.isChecked());
	}

	@Override
    protected void persist() {
		super.persist();
		file.copyToFile(getNode().getFile());
	}

	@Override
    public void validate() {
		validate(uri.isEmpty(), I18N.get("Path_is_required"));
		validate(!URI.create(uri.getText()).isAbsolute(),
				I18N.get("Path_is_required"));
	}

	private void pathSelected() {
		String pathname = uri.getValue();
		if (StringUtils.isNotBlank(pathname)) {
			String name = FilenameUtils.getName(pathname);
			if (StringUtils.isEmpty(id.getValue())) {
				id.setText(name);
			}
			if (StringUtils.isEmpty(title.getValue())) {
				title.setText(FilenameUtils.removeExtension(name));
			}
		}
	}

	/**
	 * Returns a recursive list of URIs (pathnames) for all files contained in
	 * given directory.
	 */
	private List<String> getPathnames(String directory) {
		try {
			return Files.walk(new File(directory).toPath())
					.filter(path -> path.toFile().isFile())
					.map(path -> path.toUri().toString())
					.collect(Collectors.toList());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
}
