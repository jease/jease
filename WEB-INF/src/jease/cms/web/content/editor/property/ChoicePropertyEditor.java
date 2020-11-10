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
package jease.cms.web.content.editor.property;

import jease.cms.domain.property.ChoiceProperty;
import jease.cms.domain.property.LinesProperty;
import jease.cms.domain.property.Property;
import jease.cms.service.Properties;
import jfix.util.Arrays;
import jfix.zk.Checklist;
import jfix.zk.Container;
import jfix.zk.Selectfield;

public class ChoicePropertyEditor extends Container implements
		PropertyEditor<ChoiceProperty> {

	private boolean setupProvider;
	private ChoiceProperty property;
	private Selectfield providerSelection = new Selectfield();
	private Checklist valueSelection = new Checklist();

	public ChoicePropertyEditor() {
	}

	public ChoiceProperty getProperty() {
		if (setupProvider) {
			property.setProvider((String) providerSelection
					.getSelectedValue());
		} else {
			property.setValue(Arrays.cast(valueSelection.getSelected(),
					String.class));
		}
		return property;
	}

	public void setProperty(ChoiceProperty property) {
		this.property = property;
		if (property.getProvider() != null) {
			Property provider = Properties.getByPath(property.getProvider());
			if (provider instanceof LinesProperty) {
				String[] choices = ((LinesProperty) provider).getValue();
				valueSelection.setSelection(choices, property.getValue());
				if (choices.length > 5) {
					valueSelection.orientVertical();
				} else {
					valueSelection.orientHorizontal();
				}
				setChild(valueSelection);
				return;
			}
		}
		setupProvider = true;
		providerSelection.setValues(Properties.getProviderPaths());
		setChild(providerSelection);
	}

}
