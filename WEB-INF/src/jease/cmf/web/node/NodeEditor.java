/*
    Copyright (C) 2009 maik.jablonski@gmail.com

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
package jease.cmf.web.node;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;
import jease.cmf.service.Nodes;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.i18n.Strings;
import jfix.zk.ObjectEditor;
import jfix.zk.Textfield;

public abstract class NodeEditor<E extends Node> extends ObjectEditor<E> {

	protected Textfield id = new Textfield();

	public E getNode() {
		return getObject();
	}

	protected void doInit() throws Exception {
		if (JeaseSession.getContainer() == getNode()) {
			getDeleteButton().setVisible(false);
		}
		add(Strings.Id, id);
	}

	protected void doLoad() throws Exception {
		id.setText(getNode().getId());
	}

	protected void doSave() throws Exception {
		if (getNode().getParent() == null && getNode() != Nodes.getRoot()) {
			getNode().setParent(JeaseSession.getContainer());
		}
		getNode().setId(id.getText());
	}

	protected void doValidate() throws Exception {
		if (getNode() != Nodes.getRoot()) {
			validate(id.isEmpty(), Strings.Id_is_required);
			try {
				Node parent = getNode().getParent();
				if (parent == null) {
					parent = JeaseSession.getContainer();
				}
				parent.validateChild(getNode(), id.getText());
			} catch (NodeException e) {
				addError(e.getMessage());
			}
		}
	}

	protected void doDelete() throws Exception {
		if (JeaseSession.getContainer() != getNode()) {
			delete();
		}
	}

	public void delete() {
		Nodes.delete(getNode());
	}

	protected void copyObject() {
		setObject((E) getNode().copy());
	}

}
