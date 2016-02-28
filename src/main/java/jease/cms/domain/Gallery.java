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
package jease.cms.domain;

import jease.cmf.domain.Node;
import jease.cmf.domain.NodeException;

/**
 * A Gallery is a page-like container for Images. A Gallery features a "preface"
 * which can be used as foreword. Scale is used to define the size of thumbnails
 * for the preview. If "labeled" is true, contained images will be displayed
 * with an appropriate label.
 */
public class Gallery extends Content {

	private String preface;
	private int scale;
	private boolean labeled;

	public Gallery() {
	}

	public String getPreface() {
		return preface;
	}

	public void setPreface(String text) {
		this.preface = text;
	}

	public int getScale() {
		return scale;
	}

	public void setScale(int scale) {
		this.scale = scale;
	}

	public boolean isLabeled() {
		return labeled;
	}

	public void setLabeled(boolean labeled) {
		this.labeled = labeled;
	}

	public boolean isContainer() {
		return true;
	}

	public StringBuilder getFulltext() {
		return super.getFulltext().append("\n").append(getPreface());
	}

	public Gallery copy(boolean recursive) {
		Gallery gallery = (Gallery) super.copy(recursive);
		gallery.setPreface(getPreface());
		gallery.setScale(getScale());
		gallery.setLabeled(isLabeled());
		return gallery;
	}

	public void replace(String target, String replacement) {
		super.replace(target, replacement);
		setPreface(getPreface().replace(target, replacement));
	}

	protected void validateNesting(Node potentialChild, String potentialChildId)
			throws NodeException {
		super.validateNesting(potentialChild, potentialChildId);
		if (!(potentialChild instanceof Image)) {
			throw new NodeException.IllegalNesting();
		}
	}
}
