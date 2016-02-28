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

import jease.cms.domain.Sequence;
import jfix.db4o.Database;

/**
 * Service to ease the handling of (named) sequences.
 */
public class Sequences {

	/**
	 * Returns next value for sequence given by name.
	 */
	public static int getNext(String name) {
		Sequence sequence = Sequences.query(name);
		if (sequence == null) {
			sequence = new Sequence(name);
		}
		int next = sequence.getNext();
		Database.save(sequence);
		return next;
	}

	/**
	 * Returns current value for sequence given by name.
	 */
	public static int getValue(String name) {
		Sequence sequence = Sequences.query(name);
		if (sequence == null) {
			return 0;
		} else {
			return sequence.getValue();
		}
	}

	private static Sequence query(String name) {
		if (name != null) {
			for (Sequence sequence : Database.query(Sequence.class)) {
				if (name.equals(sequence.getName())) {
					return sequence;
				}
			}
		}
		return null;
	}
}
