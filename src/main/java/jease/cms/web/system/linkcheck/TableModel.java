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
package jease.cms.web.system.linkcheck;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cms.domain.Linkcheck;
import jfix.db4o.Database;
import jfix.util.I18N;
import jfix.zk.ObjectTableModel;

public class TableModel extends ObjectTableModel<Linkcheck> {

	public Linkcheck newObject() {
		return null;
	}

	public String[] getColumns() {
		return new String[] { I18N.get("Path"), I18N.get("Link"),
				I18N.get("Status") };
	}

	public int[] getProportions() {
		return new int[] { 6, 8, 1 };
	}

	public List<Linkcheck> getList() {
		final Set<String> rootPaths = new HashSet<String>();
		for (Node root : JeaseSession.getRoots()) {
			rootPaths.add(root.getPath());
		}
		return Database.query(Linkcheck.class, new Predicate<Linkcheck>() {
			public boolean test(Linkcheck linkcheck) {
				if (Nodes.getByPath(linkcheck.getPath()) == null) {
					return false;
				}
				for (String rootPath : rootPaths) {
					if (linkcheck.getPath().startsWith(rootPath)) {
						return true;
					}
				}
				return false;
			}
		});
	}

	public Object getValue(Linkcheck linkcheck, int column) {
		switch (column) {
		case 0:
			return linkcheck.getPath();
		case 1:
			return linkcheck.getUrl();
		case 2:
			return linkcheck.getStatus();
		}
		return "";
	}

}