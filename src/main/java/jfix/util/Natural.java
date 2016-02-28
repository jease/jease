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
package jfix.util;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Provides Comparators for Natural String Order
 * 
 * The implementation was taken from:
 * http://weblogs.java.net/blog/176/2006/01/13/natural-string-order
 * 
 * Minor modifications done by Maik Jablonski.
 * 
 * 
 * Copyright (c) 2006, Stephen Kelvin Friedrich, All rights reserved.
 * 
 * This a BSD license. If you use or enhance the code, I'd be pleased if you
 * sent a mail to s.friedrich@eekboom.com
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * * Neither the name of the "Stephen Kelvin Friedrich" nor the names of its
 * contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
public class Natural<E> {

	private Natural() {
	}

	/**
	 * Returns new instance of natural comparator which uses the smart
	 * comparision of objects (see compare(o1,o2)).
	 */
	public static <E> Comparator<E> newComparator() {
		return new Comparator<E>() {
			public int compare(E o1, E o2) {
				return Natural.compare(o1, o2);
			}
		};
	}

	/**
	 * Smart compare of two objects: numbers and dates will compared by value,
	 * for all other objects a natural comparision of string-values will be
	 * used.
	 */
	public static int compare(Object o1, Object o2) {
		if (o1 instanceof Number && o2 instanceof Number) {
			return Double.compare(((Number) o1).doubleValue(),
					((Number) o2).doubleValue());
		}

		if (o1 instanceof Date && o2 instanceof Date) {
			return ((Date) o1).compareTo(((Date) o2));
		}

		return compareObjects(String.valueOf(o1), String.valueOf(o2),
				Collator.getInstance());
	}

	/**
	 * Returns a natural sorted list of given collection.
	 */
	public static <E> List<E> sort(Collection<E> collection) {
		List<E> list = new ArrayList<E>(collection);
		list.sort(Natural.newComparator());
		return list;
	}

	/**
	 * Returns a natural sorted array of given array.
	 */
	public static <E> E[] sort(E[] array) {
		E[] arrayCopy = array.clone();
		Arrays.sort(arrayCopy, Natural.newComparator());
		return arrayCopy;
	}

	/**
	 * @param s
	 *            first string
	 * @param t
	 *            second string
	 * @param collator
	 *            used to compare subwords that aren't numbers - if null,
	 *            characters will be compared individually based on their
	 *            Unicode value
	 * @return zero if <code>s</code> and <code>t</code> are equal, a value less
	 *         than zero if <code>s</code> lexicographically precedes
	 *         <code>t</code> and a value larger than zero if <code>s</code>
	 *         lexicographically follows <code>t</code>
	 */
	private static int compareObjects(String s, String t, Collator collator) {
		int sIndex = 0;
		int tIndex = 0;

		int sLength = s.length();
		int tLength = t.length();

		while (true) {
			// both character indices are after a subword (or at zero)

			// Check if one string is at end
			if (sIndex == sLength && tIndex == tLength) {
				return 0;
			}
			if (sIndex == sLength) {
				return -1;
			}
			if (tIndex == tLength) {
				return 1;
			}

			// Compare sub word
			char sChar = s.charAt(sIndex);
			char tChar = t.charAt(tIndex);

			boolean sCharIsDigit = Character.isDigit(sChar);
			boolean tCharIsDigit = Character.isDigit(tChar);

			if (sCharIsDigit && tCharIsDigit) {
				// Compare numbers

				// skip leading 0s
				int sLeadingZeroCount = 0;
				while (sChar == '0') {
					++sLeadingZeroCount;
					++sIndex;
					if (sIndex == sLength) {
						break;
					}
					sChar = s.charAt(sIndex);
				}
				int tLeadingZeroCount = 0;
				while (tChar == '0') {
					++tLeadingZeroCount;
					++tIndex;
					if (tIndex == tLength) {
						break;
					}
					tChar = t.charAt(tIndex);
				}
				boolean sAllZero = sIndex == sLength
						|| !Character.isDigit(sChar);
				boolean tAllZero = tIndex == tLength
						|| !Character.isDigit(tChar);
				if (sAllZero && tAllZero) {
					continue;
				}
				if (sAllZero && !tAllZero) {
					return -1;
				}
				if (tAllZero) {
					return 1;
				}

				int diff = 0;
				do {
					if (diff == 0) {
						diff = sChar - tChar;
					}
					++sIndex;
					++tIndex;
					if (sIndex == sLength && tIndex == tLength) {
						return diff != 0 ? diff : sLeadingZeroCount
								- tLeadingZeroCount;
					}
					if (sIndex == sLength) {
						if (diff == 0) {
							return -1;
						}
						return Character.isDigit(t.charAt(tIndex)) ? -1 : diff;
					}
					if (tIndex == tLength) {
						if (diff == 0) {
							return 1;
						}
						return Character.isDigit(s.charAt(sIndex)) ? 1 : diff;
					}
					sChar = s.charAt(sIndex);
					tChar = t.charAt(tIndex);
					sCharIsDigit = Character.isDigit(sChar);
					tCharIsDigit = Character.isDigit(tChar);
					if (!sCharIsDigit && !tCharIsDigit) {
						// both number sub words have the same length
						if (diff != 0) {
							return diff;
						}
						break;
					}
					if (!sCharIsDigit) {
						return -1;
					}
					if (!tCharIsDigit) {
						return 1;
					}
				} while (true);
			} else {
				// To use the collator the whole subwords have to be
				// compared - character-by-character comparision
				// is not possible. So find the two subwords first
				int aw = sIndex;
				int bw = tIndex;
				do {
					++sIndex;
				} while (sIndex < sLength
						&& !Character.isDigit(s.charAt(sIndex)));
				do {
					++tIndex;
				} while (tIndex < tLength
						&& !Character.isDigit(t.charAt(tIndex)));

				String as = s.substring(aw, sIndex);
				String bs = t.substring(bw, tIndex);
				int subwordResult = collator.compare(as, bs);
				if (subwordResult != 0) {
					return subwordResult;
				}
			}
		}
	}
}
