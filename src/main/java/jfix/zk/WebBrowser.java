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
package jfix.zk;

import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.SizeEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Toolbarbutton;

public class WebBrowser extends Window {

	private static final String VIEW_WIDTH = WebBrowser.class.getName() + ".Width";
	private static final String VIEW_HEIGHT = WebBrowser.class.getName() + ".Height";
	private static final String DEFAULT_HEIGHT = "95%";

	public WebBrowser(String url) {
		setTitle(url + " : ");
		setPosition("center");
		setSizable(true);
		doHighlighted();
		addEventListener(Events.ON_SIZE,
				event -> resize(((SizeEvent) event).getWidth(), ((SizeEvent) event).getHeight()));

		Button fullscreen = new Toolbarbutton("", Images.ApplicationsInternet);
		fullscreen.setHref(url);
		fullscreen.setTarget("_blank");

		Caption caption = new Caption();
		caption.appendChild(newResizeButton("XS", "360px", DEFAULT_HEIGHT));
		caption.appendChild(newResizeButton("SM", "640px", DEFAULT_HEIGHT));
		caption.appendChild(newResizeButton("MD", "768px", DEFAULT_HEIGHT));
		caption.appendChild(newResizeButton("LG", "1024px", DEFAULT_HEIGHT));
		caption.appendChild(newResizeButton("XL", "1200px", DEFAULT_HEIGHT));
		caption.appendChild(fullscreen);

		appendChild(caption);

		Iframe iframe = new Iframe();
		iframe.setWidth("100%");
		iframe.setHeight("100%");
		iframe.setSrc(url + ((url.contains("?") ? "&" : "?") + System.currentTimeMillis()));
		appendChild(iframe);

		resize(Sessions.get(VIEW_WIDTH, DEFAULT_HEIGHT), Sessions.get(VIEW_HEIGHT, DEFAULT_HEIGHT));
	}

	private void resize(String width, String height) {
		Sessions.set(VIEW_WIDTH, width);
		Sessions.set(VIEW_HEIGHT, height);
		setWidth(width);
		setHeight(height);
	}

	private Button newResizeButton(String label, String width, String height) {
		Button button = new Toolbarbutton(label);
		button.addEventListener(Events.ON_CLICK, event -> resize(width, height));
		return button;
	}
}
