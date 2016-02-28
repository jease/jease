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
package jease.cms.web.content.editor;

import jease.cms.domain.Factory;
import jease.cms.domain.Sequence;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.util.Natural;
import jfix.zk.Combobox;

public class FactoryEditor extends ContentEditor<Factory> {

	Combobox sequence = new Combobox();

	public void init() {
		add(I18N.get("Sequence"), sequence);
	}

	public void load() {
		sequence.setSelection(Natural.sort(Database.query(Sequence.class)),
				getNode().getSequence());
	}

	public void save() {
		getNode().setSequence(sequence.getText());
	}

	public void validate() {
	}
}
