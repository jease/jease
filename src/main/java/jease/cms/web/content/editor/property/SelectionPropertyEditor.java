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
package jease.cms.web.content.editor.property;

import jease.cms.domain.property.Property;
import jease.cms.domain.property.Provider;
import jease.cms.domain.property.SelectionProperty;
import jease.cms.service.Properties;
import jfix.zk.Selectfield;

public class SelectionPropertyEditor extends Selectfield implements
		PropertyEditor<SelectionProperty> {

	private boolean setupProvider;
	private SelectionProperty property;

	public SelectionProperty getProperty() {
		if (setupProvider) {
			property.setProvider((String) getSelectedValue());
		} else {
			property.setValue((String) getSelectedValue());
		}
		return property;
	}

	public void setProperty(SelectionProperty property) {
		this.property = property;
		if (property.getProvider() != null) {
			Property provider = Properties.getByPath(property.getProvider());
			if (provider instanceof Provider) {
				setModel(((Provider) provider).getValue(), property.getValue());
				return;
			}
		}
		setupProvider = true;
		setValues(Properties.getProviderPaths());
	}

}
