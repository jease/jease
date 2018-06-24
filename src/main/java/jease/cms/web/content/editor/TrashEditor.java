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

import jease.cms.domain.Trash;
import jfix.util.I18N;

public class TrashEditor extends ContentEditor<Trash> {

	public void init() {
	}

	public void load() {
		getDeleteButton().setLabel(
				getNode().isEmpty() ? I18N.get("Delete") : I18N
						.get("Empty_Trash"));
	}

	public void save() {
	}

	public void validate() {
	}
}
