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
package jease.cms.domain.property;

import org.apache.commons.lang3.StringUtils;

public class LinesProperty extends Property implements Provider {

    private String[] value;

    public LinesProperty() {
    }

    public LinesProperty(String name) {
        super(name);
    }

    public LinesProperty(String name, String[] value) {
        this(name);
        setValue(value);
    }

    public String[] getValue() {
        return value;
    }

    public void setValue(String[] value) {
        this.value = value;
    }

    public LinesProperty copy() {
        LinesProperty property = (LinesProperty) super.copy();
        property.setValue(getValue());
        return property;
    }

    public void replace(String target, String replacement) {
        super.replace(target, replacement);
        if (value != null) {
            for (int i = 0; i < value.length; i++) {
                if (value[i] != null) {
                    value[i] = value[i].replace(target, replacement);
                }
            }
        }
    }

    public String toString() {
        return StringUtils.join(value, "\n");
    }
}
