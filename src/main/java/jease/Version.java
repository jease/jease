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
package jease;

public class Version {

	private static int MAJOR = 3;
	private static int MINOR = 0;
	private static int REVISION = 0;

	public static String getName() {
		return String.format("%d.%d.%d", MAJOR, MINOR, REVISION);
	}

	public static void main(String[] args) {
		System.out.println(getName());
	}

}
