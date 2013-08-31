/*
    Copyright (C) 2013 maik.jablonski@jease.org

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

import jease.Names;
import jease.Registry;
import jease.cmf.service.Compilers;
import jfix.functor.Function;
import jfix.util.Crypts;

import org.apache.commons.lang3.StringUtils;

/**
 * Service to ease the handling of passwords (e.g. validation).
 */
public class Passwords {

	/**
	 * Default implementation for password validation. A valid password needs to
	 * contain at least 8 characters with mixed case letters, digits and
	 * symbols.
	 */
	public static class PasswordValidator implements Function<String, Boolean> {
		public Boolean evaluate(String password) {
			return Crypts.isStrongPassword(password);
		}
	}

	private static Function<String, Boolean> passwordValidator = new PasswordValidator();
	private static String passwordValidatorSource;

	/**
	 * Returns true if given password is valid.
	 * 
	 * If JEASE_PASSWORD_VALIDATOR is found in the Registry, it will be used. It
	 * must be a class which implements
	 * <code>jfix.functor.Function<String, Boolean></code>.
	 * 
	 * Otherwise a generic password validation is used: the password must
	 * contain 8 characters (mixed case letters, digits and symbols).
	 */
	public static boolean isValid(String password) {
		String newPasswordValidatorSource = Registry
				.getParameter(Names.JEASE_PASSWORD_VALIDATOR);
		if (newPasswordValidatorSource != null) {
			if (!StringUtils.equals(newPasswordValidatorSource,
					passwordValidatorSource)) {
				passwordValidatorSource = newPasswordValidatorSource;
				passwordValidator = ((Function<String, Boolean>) Compilers
						.eval(passwordValidatorSource));
			}
		}
		return passwordValidator.evaluate(password);
	}

}
