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

import java.io.IOException;
import java.net.URI;

import jfix.util.MimeTypes;
import jfix.util.Urls;

/**
 * A Transit references a file in the file-system of the server via an URI as
 * content. Therefore a Transit is quite powerful (e.g. edit JSPs directly via
 * the CMS), but it comes with security risks. A Transit should only be
 * available to very trusted people (like administrators).
 */
public class Transit extends Content {

	private String uri;
	private boolean forward;

	public Transit() {
	}

	public String getURI() {
		return uri;
	}

	public void setURI(String uri) {
		this.uri = uri;
	}

	public java.io.File getFile() {
		if (uri != null) {
			java.io.File file = new java.io.File(URI.create(uri));
			try {
				if (!file.exists()) {
					try {
						file.getParentFile().mkdirs();
						file.createNewFile();
					} catch (IOException e) {
						throw new RuntimeException(e.getMessage(), e);
					}
				}
			} catch (RuntimeException e) {
				return null;
			}
			return file;
		} else {
			return null;
		}
	}

	public String getContentType() {
		return MimeTypes.guessContentTypeFromName(uri);
	}

	public void setForward(boolean forward) {
		this.forward = forward;
	}

	public boolean isForward() {
		return forward;
	}

	public boolean isPage() {
		return !isForward();
	}

	public long getSize() {
		if (getFile() != null) {
			return super.getSize() + getFile().length();
		} else {
			return super.getSize();
		}
	}

	public Transit copy(boolean recursive) {
		Transit transit = (Transit) super.copy(recursive);
		transit.setURI(getURI());
		return transit;
	}
}
