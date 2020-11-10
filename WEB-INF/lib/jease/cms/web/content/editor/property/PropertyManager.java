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
package jease.cms.web.content.editor.property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jease.Registry;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.Factory;
import jease.cms.domain.property.Property;
import jease.cms.service.Properties;
import jfix.util.I18N;
import jfix.zk.ActionListener;
import jfix.zk.Button;
import jfix.zk.Combobox;
import jfix.zk.Formbox;
import jfix.zk.Image;
import jfix.zk.Images;
import jfix.zk.ItemRenderer;
import jfix.zk.Label;
import jfix.zk.Row;
import jfix.zk.Selectfield;
import jfix.zk.Textfield;
import jfix.zk.ZK;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.DropEvent;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;

public class PropertyManager extends Formbox {

	private List<Label> augmentableLabels = new ArrayList();
	private Selectfield typeSelect = newTypeSelect();
	private Combobox nameInput = newNameInput();
	private Button addButton = newAddButton();
	private Component controls = new Row(typeSelect, nameInput, addButton);

	public PropertyManager() {
	}

	public boolean isFactoryMode() {
		return JeaseSession.getContainer() instanceof Factory;
	}

	public void setProperties(Property[] properties) {
		getRows().getChildren().clear();
		if (properties != null) {
			for (Property property : properties) {
				appendProperty(property);
			}
		}
	}

	public Property[] getProperties() {
		List<Property> properties = new ArrayList();
		for (Component component : ZK.getDescendants(this)) {
			if (component instanceof PropertyEditor) {
				Property property = ((PropertyEditor) component).getProperty();
				if (isFactoryMode() && property.getSerial() == 0) {
					property.initSerial();
				}
				properties.add(property);
			}
		}
		return properties.toArray(new Property[] {});
	}

	public void toogleEdit() {
		if (contains(controls)) {
			setProperties(getProperties());
		} else {
			appendControls();
		}
	}

	private void appendProperty(Property property) {
		add(I18N.get(property.getName()), newPropertyEditor(property));
	}

	private Button newAddButton() {
		Button button = new Button("", Images.ListAdd);
		button.setHflex("1");
		button.addClickListener(new ActionListener() {
			public void actionPerformed(Event event) {
				addPerformed();
			}
		});
		return button;
	}

	private void addPerformed() {
		if (!(typeSelect.isEmpty() || nameInput.isEmpty())) {
			Property property = ((Property) typeSelect.getSelectedValue())
					.copy();
			property.setName(nameInput.getValue());
			appendProperty(property);
			appendControls();
		}
	}

	private Selectfield newTypeSelect() {
		Selectfield select = new Selectfield(Properties.getAvailableTypes());
		select.setItemRenderer(new ItemRenderer() {
			public String render(Object value) {
				return value != null ? ((Property) value).getType() : null;
			}
		});
		select.setHflex("3");
		return select;
	}

	private Combobox newNameInput() {
		Combobox input = new Combobox();
		input.setSelection(Arrays.asList(Properties.getPropertyNames()), null);
		input.setHflex("8");
		return input;
	}

	private void appendControls() {
		remove(controls);
		add("", controls);
		Image trash = new Image(Images.UserTrashFull);
		trash.setDroppable(toString());
		trash.addEventListener(Events.ON_DROP, new EventListener() {
			public void onEvent(Event evt) throws Exception {
				DropEvent dropEvent = (DropEvent) evt;
				getRows().removeChild(
						dropEvent.getDragged().getParent().getParent());
			}
		});
		trash.setParent(controls.getParent().getPreviousSibling());
		augmentLablesForEditing();
	}

	private void augmentLablesForEditing() {
		for (final Label label : augmentableLabels) {
			if (label.getStyle() != null) {
				continue;
			}
			label.setValue(((PropertyEditor) label.getParent().getNextSibling()
					.getFirstChild()).getProperty().getName());
			label.setStyle("cursor: pointer; font-style: italic;");
			label.setDraggable(toString());
			label.setDroppable(toString());
			label.addEventListener(Events.ON_DROP, new EventListener() {
				public void onEvent(Event evt) throws Exception {
					DropEvent dropEvent = (DropEvent) evt;
					swapRows(dropEvent.getDragged(), dropEvent.getTarget());
				}
			});
			label.addEventListener(Events.ON_CLICK, new EventListener() {
				public void onEvent(Event evt) throws Exception {
					convertLabelToTextfield(label);
				}
			});
		}
	}

	private void swapRows(Component dragged, Component target) {
		getRows().insertBefore(dragged.getParent().getParent(),
				target.getParent().getParent());
	}

	private void convertLabelToTextfield(final Label label) {
		final Textfield textfield = new Textfield(label.getValue());
		EventListener labelEditPerformed = new EventListener() {
			public void onEvent(Event evt) throws Exception {
				String value = textfield.getValue();
				label.setValue(value);
				((PropertyEditor) textfield.getParent().getNextSibling()
						.getFirstChild()).getProperty().setName(value);
				ZK.replace(textfield, label);
			}
		};
		textfield.addEventListener(Events.ON_OK, labelEditPerformed);
		textfield.addEventListener(Events.ON_BLUR, labelEditPerformed);
		ZK.replace(label, textfield);
		textfield.focus();
	}

	private PropertyEditor newPropertyEditor(Property property) {
		PropertyEditor editor = Registry.getEditor(property);
		editor.setProperty(property.copy());
		return editor;
	}

	protected Label newLabel(String name, Component component) {
		Label label = super.newLabel(name, component);
		if (component instanceof PropertyEditor
				&& (((PropertyEditor) component).getProperty().getSerial() == 0 || isFactoryMode())) {
			augmentableLabels.add(label);
		}
		return label;
	}
}
