package jease.cms.domain;

import jfix.util.Urls;

/**
 * A Transit references a file in the file-system of the server as content.
 * Therefore a Transit is quite powerful (e.g. edit JSPs directly via the CMS),
 * but it comes with security risks. Therefore a Transit should only be
 * available to very trusted people (like administrators).
 */
public class Transit extends Content {

	private String pathname;

	public Transit() {
	}

	public String getPathname() {
		return pathname;
	}

	public void setPathname(String pathname) {
		this.pathname = pathname;
	}

	public java.io.File getFile() {
		return new java.io.File(pathname);
	}

	public String getContentType() {
		return Urls.guessContentTypeFromName(pathname);
	}

	public boolean isPage() {
		return false;
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
		transit.setPathname(getPathname());
		return transit;
	}
}
