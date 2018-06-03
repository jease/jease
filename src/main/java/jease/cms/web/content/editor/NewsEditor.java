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

import jease.cms.domain.News;
import jease.cms.web.component.RichTextarea;
import jfix.util.I18N;
import jfix.zk.Column;

import org.apache.commons.lang3.StringUtils;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Textbox;

public class NewsEditor extends ContentEditor<News> {

	Textbox teaser = new Textbox();
	RichTextarea story = new RichTextarea();
	Checkbox emptyTeaser = new Checkbox(I18N.get("Empty_Teaser"));
	Datebox date = new Datebox();

	public NewsEditor() {
		teaser.setRows(3);
		story.setHeight((getDesktopHeight() / 3 - 50) + "px");
		emptyTeaser.addEventListener(Events.ON_CHECK,
				event -> emptyTeaserChecked(emptyTeaser.isChecked()));
		date.setLenient(false);
		date.setFormat("short");
	}

	public void init() {
		add(I18N.get("Teaser"), new Column(teaser, emptyTeaser));
		add(I18N.get("Story"), story);
		add(I18N.get("Date"), date);
	}

	public void load() {
		teaser.setText(getNode().getTeaser());
		story.setText(getNode().getStory());
		date.setValue(getNode().getDate());
		emptyTeaserChecked(StringUtils.isEmpty(teaser.getValue()));
	}

	public void save() {
		getNode().setTeaser(teaser.getText());
		getNode().setStory(story.getText());
		getNode().setDate(date.getValue());
	}

	public void validate() {
		validate(story.isEmpty(), I18N.get("Story_is_required"));
	}

	private void emptyTeaserChecked(boolean empty) {
		if (empty) {
			teaser.setText("");
		}
		teaser.setVisible(!empty);
		emptyTeaser.setChecked(empty);
	}
}
