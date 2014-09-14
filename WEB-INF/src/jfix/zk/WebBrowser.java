/*
    Copyright (C) 2014 maik.jablonski@jease.org

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
package jfix.zk;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Toolbarbutton;

public class WebBrowser extends Window {

	private static final String VIEW_WIDTH = WebBrowser.class.getName()
			+ ".Width";
	private static final String VIEW_HEIGHT = WebBrowser.class.getName()
			+ ".Height";
	private static String DEFAULT_SIZE = "95%";

	public WebBrowser(String url) {
		setTitle(url);
		setPosition("center");
		setSizable(true);
		doHighlighted();
		addEventListener(
				Events.ON_SIZE,
				event -> resize(((SizeEvent) event).getWidth(),
						((SizeEvent) event).getHeight()));

		Button fullscreen = new Toolbarbutton(null, Images.ViewFullscreen);
		fullscreen.setHref(url);
		fullscreen.setTarget("_blank");

		Caption caption = new Caption();
		caption.appendChild(fullscreen);
		caption.appendChild(newResizeButton("800x600", "800px", "600px"));
		caption.appendChild(newResizeButton("1024x768", "1024px", "768px"));
		caption.appendChild(newResizeButton("1280x1024", "1280px", "1024px"));
		appendChild(caption);

		Iframe iframe = new Iframe();
		iframe.setWidth("100%");
		iframe.setHeight("100%");
		iframe.setSrc(url
				+ ((url.contains("?") ? "&" : "?") + System.currentTimeMillis()));
		appendChild(iframe);

		resize(Sessions.get(VIEW_WIDTH, DEFAULT_SIZE),
				Sessions.get(VIEW_HEIGHT, DEFAULT_SIZE));
	}

	private void resize(String width, String height) {
		Sessions.set(VIEW_WIDTH, width);
		Sessions.set(VIEW_HEIGHT, height);
		setWidth(width);
		setHeight(height);
	}

	private Button newResizeButton(final String label, final String width,
			final String height) {
		Button button = new Toolbarbutton(label);
		button.addEventListener(Events.ON_CLICK, event -> resize(width, height));
		return button;
	}
}
