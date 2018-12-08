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

import jease.cms.domain.property.LinkProperty;
import jease.cms.web.component.Linkfield;

public class LinkPropertyEditor extends Linkfield implements
        PropertyEditor<LinkProperty> {

    private LinkProperty property;

    public LinkProperty getProperty() {
        property.setValue(getValue());
        return property;
    }

    public void setProperty(LinkProperty property) {
        this.property = property;
        setValue(property.getValue());
    }

}
