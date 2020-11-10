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
