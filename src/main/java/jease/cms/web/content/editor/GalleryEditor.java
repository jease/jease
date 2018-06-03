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

import jease.cms.domain.Gallery;
import jease.cms.web.component.RichTextarea;
import jfix.util.I18N;
import jfix.zk.Div;
import jfix.zk.Spinner;

import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Label;

public class GalleryEditor extends ContentEditor<Gallery> {

	RichTextarea preface = new RichTextarea();
	Spinner scale = new Spinner();
	Checkbox labeled = new Checkbox();

	public GalleryEditor() {
		preface.setHeight((getDesktopHeight() / 3 - 50) + "px");
		scale.setConstraint("min 0,no empty");
		scale.setStep(25);
	}

	public void init() {
		add(I18N.get("Preface"), preface);
		add(I18N.get("Scale"), new Div(scale, new Label(" px")));
		add(I18N.get("Labeled"), labeled);
	}

	public void load() {
		preface.setValue(getNode().getPreface());
		scale.setValue(getNode().getScale());
		labeled.setChecked(getNode().isLabeled());
	}

	public void save() {
		getNode().setPreface(preface.getValue());
		getNode().setScale(scale.intValue());
		getNode().setLabeled(labeled.isChecked());
	}

	public void validate() {
		validate(scale.intValue() < 1, I18N.get("Scale_is_not_valid"));
	}

}
