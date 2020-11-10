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
import jease.cmf.web.node.NodeEditor;
import jease.cmf.web.node.NodeTableModel;
import jease.cms.domain.File;
import jease.cms.domain.Folder;
import jease.cms.domain.Image;
import jease.cms.domain.Link;
import jease.cms.domain.News;
import jease.cms.domain.Reference;
import jease.cms.domain.Text;
import jease.cms.domain.Topic;
import jease.cms.web.content.editor.FileEditor;
import jease.cms.web.content.editor.FolderEditor;
import jease.cms.web.content.editor.ImageEditor;
import jease.cms.web.content.editor.LinkEditor;
import jease.cms.web.content.editor.NewsEditor;
import jease.cms.web.content.editor.ReferenceEditor;
import jease.cms.web.content.editor.TextEditor;
import jease.cms.web.content.editor.TopicEditor;

/**
 * Global configuration for JeaseCMS.
 */
public class Configuration extends JeaseConfig {

	/**
	 * Which type of nodes can be created by the user?
	 */
	public Node[] newNodes() {
		return new Node[] { new Text(), new News(), new Folder(), new Image(),
				new File(), new Link(), new Topic(), new Reference() };
	}

	/**
	 * Which editor should be used to edit a given node?
	 */
	public NodeEditor newEditor(Node node) {
		if (node instanceof Text) {
			return new TextEditor();
		}
		if (node instanceof News) {
			return new NewsEditor();
		}
		if (node instanceof Folder) {
			return new FolderEditor();
		}
		if (node instanceof Image) {
			return new ImageEditor();
		}
		if (node instanceof File) {
			return new FileEditor();
		}
		if (node instanceof Link) {
			return new LinkEditor();
		}
		if (node instanceof Topic) {
			return new TopicEditor();
		}
		if (node instanceof Reference) {
			return new ReferenceEditor();
		}
		return null;
	}

	/**
	 * Which TableModel should be used to render the contents of a container node?
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
