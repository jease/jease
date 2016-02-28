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
package jease.cms.web.content.editor;

import jease.cms.domain.Discussion;
import jfix.util.I18N;

import org.zkoss.zul.Textbox;

public class DiscussionEditor extends ContentEditor<Discussion> {

	Textbox author = new Textbox();
	Textbox comment = new Textbox();

	public DiscussionEditor() {
		comment.setRows(10);
	}

	public void init() {
		add(I18N.get("Author"), author);
		add(I18N.get("Comment"), comment);
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
