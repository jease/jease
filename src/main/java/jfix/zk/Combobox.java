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
package jfix.zk;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zul.ListModelList;

public class Combobox extends org.zkoss.zul.Combobox {

	public Combobox() {
		setHflex("1");
	}

	public void setSelection(List<?> values, String selected) {
		setModel(new ListModelList<Object>(values));
		if (selected != null) {
			setValue(selected);
		}
	}

	public boolean isEmpty() {
		return StringUtils.isBlank(getValue());
	}

}
