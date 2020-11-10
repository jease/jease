package jease.cms.domain;

import java.net.URI;

import jfix.util.Files;
import jfix.util.Urls;

/**
 * A Transit references a file in the file-system of the server via an URI as
 * content. Therefore a Transit is quite powerful (e.g. edit JSPs directly via
 * the CMS), but it comes with security risks. A Transit should only be
 * available to very trusted people (like administrators).
 */
public class Transit extends Content {

	private String uri;

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
				Files.createMissing(file);
			} catch (RuntimeException e) {
				// pass when create fails
			}
			return file;
		} else {
			return null;
		}
	}

	public String getContentType() {
		return Urls.guessContentTypeFromName(uri);
	}

	public boolean isPage() {
		return getContentType().equals("text/html");
	}

	/**
	 * A Transit can access the file-system of the server, therefore only
	 * privileged users should be able to use them.
	 */
	public boolean isPrivileged() {
		return true;
	}

	public long getSize() {
		return super.getSize() + getFile().length();
	}

	public Transit copy() {
		Transit transit = (Transit) super.copy();
		transit.setURI(getURI());
		return transit;
	}
}
