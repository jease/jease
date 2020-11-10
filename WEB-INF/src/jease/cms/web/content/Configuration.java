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
package jease.cms.web.content;

import jease.cmf.domain.Node;
import jease.cmf.web.JeaseConfig;
import jease.cmf.web.JeaseSession;
import jease.cmf.web.node.NodeEditor;
import jease.cmf.web.node.NodeTableModel;
import jease.cms.domain.Content;
import jease.cms.domain.User;
import jease.cms.service.Contents;
import jease.cms.web.content.editor.ContentEditor;
import jfix.util.Reflections;

/**
 * Global configuration for JeaseCMS.
 */
public class Configuration extends JeaseConfig {

	/**
	 * Which type of nodes can be created by the user? We use reflection to find
	 * all Nodes in the default Content-Package. If you want maximum control,
	 * you can also create a config by hand:
	 * 
	 * <code> 
	 * return new Node[] { new Folder(), new Text(), new File(), ... };
	 * </code>
	 */
	public Node[] newNodes() {
		Node[] nodes = Reflections.find(Node.class, Content.class.getPackage());
		if (JeaseSession.get(User.class).isAdministrator()) {
			return nodes;
		} else {
			return Contents.filterNotPrivileged(nodes);
		}
	}

	/**
	 * Which editor should be used to edit a given node? We use reflection and
	 * convention (editor-class needs to end on Editor) to find an editor for a
	 * given Node. If you want maximum control, you can also create a config by
	 * hand:
	 * 
	 * <code> 
	 * if (node instanceof Folder) return new FolderEditor();
	 * if (node instanceof Text) return new TextEditor();
	 * if (node instanceof File) return new FileEditor();
	 * ...
	 * </code>
	 */
	public NodeEditor newEditor(Node node) {
		String pckage = ContentEditor.class.getPackage().getName();
		String clazz = String.format("%s.%sEditor", pckage, node.getType());
		return (NodeEditor) Reflections.newInstance(clazz);
	}

	/**
	 * Which TableModel should be used to render the contents of a container
	 * node?
	 */
	public NodeTableModel newTableModel() {
		return new ContentTableModel();
	}

	/**
	 * Which icon should be displayed in front of a given node?
	 */
	public String getIcon(Node node) {
		return String.format("~./jease/cms/%s.png", node.getType());
	}

}
