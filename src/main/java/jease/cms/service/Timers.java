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
package jease.cms.service;

import java.util.Timer;
import java.util.TimerTask;

import jease.Names;
import jease.Registry;
import jfix.util.Reflections;

import org.apache.commons.lang3.StringUtils;

/**
 * Implements a simple Timer which runs a runnable task from the Registry for
 * every second.
 */
public class Timers {

	private static Timer timer;
	private static String timerTaskClass;
	private static Runnable runnable;

	public static void start() {
		stop();
		timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					String newTimerTaskClass = Registry
							.getParameter(Names.JEASE_TIMER_TASK);
					if (StringUtils.isNotBlank(newTimerTaskClass)) {
						if (!StringUtils.equals(timerTaskClass,
								newTimerTaskClass)) {
							timerTaskClass = newTimerTaskClass;
							runnable = (Runnable) Reflections
									.newInstance(timerTaskClass);
						}
						if (runnable != null) {
							runnable.run();
						}
					}
				} catch (Throwable e) {
					timerTaskClass = null;
					runnable = null;
				}
			}
		}, 1000, 1000);
	}

	public static void stop() {
		if (timer != null) {
			timer.cancel();
			timerTaskClass = null;
			runnable = null;
		}
	}

}
