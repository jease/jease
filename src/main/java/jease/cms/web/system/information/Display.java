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
package jease.cms.web.system.information;

import java.util.Map;

import jease.cms.service.Informatons;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Formbox;
import jfix.zk.Refreshable;

import org.zkoss.zul.Label;

public class Display extends Div implements Refreshable {

	public void refresh() {
		getChildren().clear();
		Formbox form = new Formbox();
		form.add(I18N.get("Version"));
		form.add(I18N.get("Jease"), new Label(jease.Version.getName()));
		form.add(I18N.get("ZK"), new Label(org.zkoss.zk.Version.RELEASE));
		form.add(I18N.get("Database"));
		form.add(I18N.get("Path"), new Label(Informatons.getDatabaseDirectory()));
		form.add(I18N.get("Size"), new Label(Informatons.getDatabaseSize()));
		form.add(I18N.get("Objects"));
		form.add(I18N.get("Count"), new Label("" + Informatons.getDatabaseObjectCount().size()));
		Map<Class<?>, Integer> classCount = Informatons.getDatabaseClassCount();
		for (Class<?> clazz : classCount.keySet()) {
			form.add("- " + clazz.getName(), new Label("" + classCount.get(clazz)));
		}
		appendChild(form);
	}

}
