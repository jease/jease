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

import jease.cms.domain.Gallery;
import jease.cms.web.i18n.Strings;
import jfix.zk.Checkbox;
import jfix.zk.RichTextarea;
import jfix.zk.Spinner;

public class GalleryEditor extends ContentEditor<Gallery> {

	RichTextarea preface = new RichTextarea();
	Spinner scale = new Spinner();
	Checkbox labeled = new Checkbox();
	
	public GalleryEditor() {
		scale.setConstraint("min 0,no empty");
		scale.setStep(25);
	}

	public void init() {
		add(Strings.Preface, preface);
		add(Strings.Scale, scale);
		add(Strings.Labeled, labeled);
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
		validate(scale.intValue() < 1, Strings.Scale_is_not_valid);
	}

}
