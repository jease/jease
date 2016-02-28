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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * Common utility methods to handle urls.
 */
public class Urls {

	/**
	 * Translates given string into application/x-www-form-urlencoded format
	 * using a specific encoding scheme.
	 */
	public static String encode(String str, String enc) {
		try {
			return URLEncoder.encode(str, enc);
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * Decodes given application/x-www-form-urlencoded string using a specific
	 * encoding scheme.
	 */
	public static String decode(String str, String enc) {
		try {
			return URLDecoder.decode(str, enc);
		} catch (Exception e) {
			return str;
		}
	}

	/**
	 * Check is given url is valid (= url needs no encoding).
	 */
	public static boolean isValid(String url) {
		return isValid(url, "UTF-8");
	}

	/**
	 * Check is given url is valid for given encoding (= url needs no encoding).
	 */
	public static boolean isValid(String url, String enc) {
		return StringUtils.isNotBlank(url) && url.equals(Urls.encode(url, enc));
	}

	/**
	 * Returns content from given url as string. The url can contain
	 * username:password after the protocol, so that basic authorization is
	 * possible.
	 * 
	 * Example for url with basic authorization:
	 * 
	 * http://username:password@www.domain.org/index.html
	 */
	public static String readString(String url, int timeout) {
		Reader reader = null;
		try {
			URLConnection uc = new URL(url).openConnection();
			if (uc instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) uc;
				httpConnection.setConnectTimeout(timeout * 1000);
				httpConnection.setReadTimeout(timeout * 1000);
			}
			Matcher matcher = Pattern.compile("://(\\w+:\\w+)@").matcher(url);
			if (matcher.find()) {
				String auth = matcher.group(1);
				String encoding = Base64.getEncoder().encodeToString(
						auth.getBytes());
				uc.setRequestProperty("Authorization", "Basic " + encoding);
			}
			String charset = (uc.getContentType() != null && uc
					.getContentType().contains("charset=")) ? uc
					.getContentType().split("charset=")[1] : "utf-8";
			reader = new BufferedReader(new InputStreamReader(
					uc.getInputStream(), charset));
			StringBuilder sb = new StringBuilder();
			for (int chr; (chr = reader.read()) != -1;) {
				sb.append((char) chr);
			}
			return sb.toString();
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
		}
	}

	/**
	 * Returns true if given url can be connected via HTTP within given timeout
	 * (specified in seconds). Otherwise the url might be broken.
	 */
	public static boolean isConnectable(String url, int timeout) {
		try {
			URLConnection connection = new URL(url).openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setConnectTimeout(timeout * 1000);
				httpConnection.setReadTimeout(timeout * 1000);
				httpConnection.connect();
				int response = httpConnection.getResponseCode();
				httpConnection.disconnect();
				return response == HttpURLConnection.HTTP_OK;
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	/**
	 * Returns the status code for connecting given url with given timeout.
	 * Returns 0 if an IOException occurs.
	 */
	public static int getStatus(String url, int timeout) {
		try {
			URLConnection connection = new URL(url).openConnection();
			if (connection instanceof HttpURLConnection) {
				HttpURLConnection httpConnection = (HttpURLConnection) connection;
				httpConnection.setConnectTimeout(timeout * 1000);
				httpConnection.setReadTimeout(timeout * 1000);
				httpConnection.connect();
				int response = httpConnection.getResponseCode();
				httpConnection.disconnect();
				return response;
			}
		} catch (IOException e) {
			// pass
		}
		return 0;
	}

}
