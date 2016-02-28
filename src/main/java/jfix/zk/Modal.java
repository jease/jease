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

import java.io.PrintWriter;
import java.io.StringWriter;

import jfix.util.I18N;

import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zul.Messagebox;

public class Modal {

	public static void info(String message) {
		Messagebox.show(message, I18N.get("Information"), Messagebox.OK,
				Messagebox.INFORMATION);
	}

	public static void info(String message, final EventListener<Event> action) {
		Messagebox
				.show(message,
						I18N.get("Information"),
						Messagebox.OK,
						Messagebox.INFORMATION,
						$event -> {
							if (((Integer) $event.getData()).intValue() == Messagebox.OK) {
								action.onEvent($event);
							}
						});
	}

	public static void error(String message) {
		Messagebox.show(message, I18N.get("Error"), Messagebox.OK,
				Messagebox.ERROR);
	}

	public static void error(String message, final EventListener<Event> action) {
		Messagebox
				.show(message,
						I18N.get("Error"),
						Messagebox.OK,
						Messagebox.ERROR,
						$event -> {
							if (((Integer) $event.getData()).intValue() == Messagebox.OK) {
								action.onEvent($event);
							}
						});
	}

	public static void confirm(String message, final EventListener<Event> action) {
		Messagebox.show(message, I18N.get("Confirm"), Messagebox.OK
				| Messagebox.CANCEL, Messagebox.QUESTION, $event -> {
			if (((Integer) $event.getData()).intValue() == Messagebox.OK) {
				action.onEvent($event);
			}
		});
	}

	/**
	 * Performs the given action for the given event for the given component in
	 * a modal way by locking the UI for short moment by showing the busy
	 * indicator.
	 */
	public static void click(final Component component, final Event event,
			final EventListener<Event> action) {
		ZK.showBusy();
		component.addEventListener("onModalAction", new EventListener<Event>() {
			public void onEvent(Event modalEvent) throws Exception {
				// Clean up before running the listener, because we don't know
				// what the listener will do (e.g. remove/replace component
				// which triggered the action). The UI will be locked until the
				// listener returns.
				component.removeEventListener("onModalAction", this);
				ZK.clearBusy();
				action.onEvent(event);
			}
		});
		Events.echoEvent("onModalAction", component, null);
	}

	/**
	 * Displays the stacktrace of given exception in modal window.
	 */
	public static void exception(Throwable e) {
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		Modal.error(stringWriter.toString());
	}
}
