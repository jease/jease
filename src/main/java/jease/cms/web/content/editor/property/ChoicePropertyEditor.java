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

import java.lang.reflect.Array;
import java.util.Arrays;

import jease.cms.domain.property.ChoiceProperty;
import jease.cms.domain.property.Property;
import jease.cms.domain.property.Provider;
import jease.cms.service.Properties;
import jfix.zk.Checklist;
import jfix.zk.Container;
import jfix.zk.Scrollbox;
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
			property.setProvider((String) providerSelection.getSelectedValue());
		} else {
			property.setValue(Arrays.stream(valueSelection.getSelected())
					.toArray(
							size -> (String[]) Array.newInstance(String.class,
									size)));
		}
		return property;
	}

	public void setProperty(ChoiceProperty property) {
		this.property = property;
		if (property.getProvider() != null) {
			Property provider = Properties.getByPath(property.getProvider());
			if (provider instanceof Provider) {
				String[] choices = ((Provider) provider).getValue();
				valueSelection.setSelection(choices, property.getValue());
				valueSelection.orientVertical();
				if (choices.length > 15) {
					setChild(new Scrollbox(valueSelection));
				} else {
					setChild(valueSelection);
				}
				return;
			}
		}
		setupProvider = true;
		providerSelection.setValues(Properties.getProviderPaths());
		setChild(providerSelection);
	}

}
