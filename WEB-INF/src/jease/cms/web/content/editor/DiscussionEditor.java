/*
    Copyright (C) 2011 maik.jablonski@gmail.com

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
package jease.cms.web.content.editor;

import jease.cms.domain.Discussion;
import jease.cms.web.i18n.Strings;
import jfix.zk.Textarea;
import jfix.zk.Textfield;

public class DiscussionEditor extends ContentEditor<Discussion> {

	Textfield author = new Textfield();
	Textarea comment = new Textarea();
	
	public DiscussionEditor() {
		comment.setRows(10);
	}
	
	public void init() {
		add(Strings.Author, author);
		add(Strings.Comment, comment);
	}

	public void load() {
		author.setText(getNode().getAuthor());
		comment.setText(getNode().getComment());
	}

	public void save() {
		getNode().setAuthor(author.getText());
		getNode().setComment(comment.getText());
	}

	public void validate() {
	}

}
