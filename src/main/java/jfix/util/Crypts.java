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

import java.security.MessageDigest;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

/**
 * Common utility methods to encrypt/decrypt numbers and strings.
 */
public class Crypts {

	private static final char[] HEX_CHARACTERS = { '0', '1', '2', '3', '4',
			'5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };

	/**
	 * Creates a strong password with given length which contains lower and
	 * upercase letters, digits and symbols.
	 */
	public static String generatePassword(int length) {
		final String alpha = "abcdefghijklmnopqrstuvwxyz";
		final String digits = "0123456789";
		final String symbols = "!$%&/()*+#-_.:,;<>";
		final Random random = new Random();

		List<Character> password = new ArrayList<Character>();
		for (int i = 0; i < length; i++) {
			password.add(alpha.charAt(random.nextInt(alpha.length())));
			password.add(alpha.toUpperCase().charAt(
					random.nextInt(alpha.length())));
			password.add(digits.charAt(random.nextInt(digits.length())));
			password.add(symbols.charAt(random.nextInt(symbols.length())));
		}

		String result = "";
		while (!isStrongPassword(result)) {
			Collections.shuffle(password);
			StringBuilder sb = new StringBuilder();
			for (Character c : password) {
				sb.append(c);
			}
			result = sb.toString().substring(0, length);
		}
		return result;
	}

	/**
	 * Returns true if password is at least 8 characters and contains lower and
	 * uppercase letters, digits and symbols.
	 */
	public static boolean isStrongPassword(String password) {
		if (password == null || password.length() < 8) {
			return false;
		}
		if (!password.matches(".*[a-z]+.*")) {
			return false;
		}
		if (!password.matches(".*[A-Z]+.*")) {
			return false;
		}
		if (!password.matches(".*[\\d]+.*")) {
			return false;
		}
		if (!password.matches(".*[^a-zA-Z\\d]+.*")) {
			return false;
		}
		return true;
	}

	/**
	 * Encrypts griven string with given pass-phrase with DES.
	 */
	public static String cipher(String msg, byte[] passPhrase) {
		try {
			KeySpec keySpec = new DESKeySpec(passPhrase);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
					keySpec);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return toHexString(cipher.doFinal(msg.getBytes()));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Decrypts griven string with given pass-phrase with DES.
	 */
	public static String decipher(String msg, byte[] passPhrase) {
		try {
			KeySpec keySpec = new DESKeySpec(passPhrase);
			SecretKey key = SecretKeyFactory.getInstance("DES").generateSecret(
					keySpec);
			Cipher cipher = Cipher.getInstance(key.getAlgorithm());
			cipher.init(Cipher.DECRYPT_MODE, key);
			return new String(cipher.doFinal(fromHexString(msg)));
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Creates a md5 encoded in base64 for given input.
	 */
	public static String md5(byte[] input) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			md5.reset();
			md5.update(input);
			return Base64.getEncoder().encodeToString(md5.digest());
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
	}

	/**
	 * Creates a random pass-phrase suitable for DES-encryption.
	 */
	public static byte[] createRandomPassPhrase() {
		byte[] passPhrase = String.valueOf(Math.random()).getBytes();
		if (passPhrase.length < 8) {
			return createRandomPassPhrase();
		}
		return passPhrase;
	}

	/**
	 * Converts given byte-array into hexadecimal.
	 */
	public static String toHexString(byte[] b) {
		StringBuffer sb = new StringBuffer(b.length * 2);
		for (int i = 0; i < b.length; i++) {
			sb.append(HEX_CHARACTERS[(b[i] & 0xf0) >>> 4]);
			sb.append(HEX_CHARACTERS[b[i] & 0x0f]);
		}
		return sb.toString();
	}

	/**
	 * Converts given hexadecimal into byte-array.
	 */
	public static byte[] fromHexString(String s) {
		int stringLength = s.length();
		if ((stringLength % 2) != 0) {
			throw new IllegalArgumentException(
					"Even number of characters required");
		}
		byte[] b = new byte[stringLength / 2];
		for (int i = 0, j = 0; i < stringLength; i += 2, j++) {
			int high = charToNibble(s.charAt(i));
			int low = charToNibble(s.charAt(i + 1));
			b[j] = (byte) ((high << 4) | low);
		}
		return b;
	}

	private static int charToNibble(char c) {
		if ('0' <= c && c <= '9') {
			return c - '0';
		} else if ('a' <= c && c <= 'f') {
			return c - 'a' + 0xa;
		} else if ('A' <= c && c <= 'F') {
			return c - 'A' + 0xa;
		} else {
			throw new IllegalArgumentException("Invalid hex character: " + c);
		}
	}

}
