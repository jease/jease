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
package jease.cms.web.content;

import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.web.JeaseConfig;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cmf.web.node.NodeTableModel;
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jease.cms.service.Relocator;

/**
 * Global configuration for JeaseCMS.
 */
public class Configuration implements JeaseConfig {

	static {
		Content.setPathChangeProcessor(new Relocator());
	}

	/**
	 * Which type of nodes can be created by the user? If you want maximum
	 * control, you can also create a config by hand:
	 * 
	 * <code> 
	 * return new Node[] { new Folder(), new Text(), new File(), ... };
	 * </code>
	 */
	public Node[] newNodes() {
		if (JeaseSession.get(User.class).getRole() != null) {
			return JeaseSession.get(User.class).getRole().getNodes();
		} else {
			return new Node[] {};
		}
	}

	/**
	 * Which editor should be used to edit a given node? If you want maximum
	 * control, you can also create a config by hand:
	 * 
	 * <code> 
	 * if (node instanceof Folder) return new FolderEditor();
	 * if (node instanceof Text) return new TextEditor();
	 * if (node instanceof File) return new FileEditor();
	 * ...
	 * </code>
	 */
	public NodeEditor<Node> newEditor(Node node) {
		return Registry.getEditor(node);
	}

	/**
	 * Which TableModel should be used to render the contents of a container
	 * node?
	 */
	public NodeTableModel<Node> newTableModel() {
		return new ContentTableModel();
	}

	/**
	 * Which icon should be displayed in front of a given node?
	 */
	public String getIcon(Node node) {
		return Registry.getIcon(node);
	}

}
