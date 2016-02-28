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

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class I18N {

	private static ResourceBundle bundle;

	static {
		load(Locale.getDefault());
	}

	public static void load(Locale locale) {
		bundle = new MessageBundle("META-INF.i18n.msg", locale);
	}

	public static String get(String key) {
		if (key == null) {
			return null;
		}
		if (bundle.containsKey(key)) {
			return bundle.getString(key);
		} else {
			return key.replace("_", " ");
		}
	}

	static private class MessageBundle extends ResourceBundle {

		private String baseName;
		private String packageName;
		private Map<String, Object> messages;

		protected MessageBundle(String resource, Locale locale) {
			int idx = resource.lastIndexOf(".");
			this.baseName = resource.substring(idx + 1);
			this.packageName = resource.substring(0, idx);
			loadBundles(locale);
		}

		public Object handleGetObject(String key) {
			if (key == null) {
				throw new NullPointerException();
			}
			return messages.get(key);
		}

		public Enumeration<String> getKeys() {
			ResourceBundle parent = this.parent;
			return new ResourceBundleEnumeration(messages.keySet(),
					(parent != null) ? parent.getKeys() : null);
		}

		private void loadBundles(Locale locale) {
			messages = new HashMap<String, Object>();
			for (String bundleName : getBundleNames(baseName)) {
				ResourceBundle bundle = ResourceBundle.getBundle(bundleName,
						locale);
				Enumeration<String> keys = bundle.getKeys();
				while (keys.hasMoreElements()) {
					String key = keys.nextElement();
					messages.put(key, bundle.getObject(key));
				}
			}
		}

		private List<String> getBundleNames(String baseName) {
			List<String> bundleNames = new ArrayList<String>();
			try {
				String baseFileName = baseName + ".properties";
				String resourcePath = getResourcePath();
				String resourceName = resourcePath + baseFileName;
				Enumeration<URL> names = Thread.currentThread()
						.getContextClassLoader().getResources(resourceName);
				while (names.hasMoreElements()) {
					URL jarUrl = names.nextElement();
					String jarFile = URLDecoder.decode(jarUrl.getFile(),
							"UTF-8");
					if ("jar".equals(jarUrl.getProtocol())) {
						String filename = jarFile.substring(0, jarFile.length()
								- resourceName.length() - 2);
						if (filename.startsWith("file:")) {
							filename = filename.substring(5);
						}
						JarFile jar = new JarFile(filename);
						for (Enumeration<JarEntry> entries = jar.entries(); entries
								.hasMoreElements();) {
							JarEntry entry = entries.nextElement();
							String name = entry.getName();
							addMatch("", baseName, bundleNames, baseFileName,
									name);
						}
						jar.close();
					} else {
						File jarDirectory = new File(jarFile).getParentFile();
						if (jarDirectory.isDirectory()) {
							for (String name : jarDirectory.list()) {
								addMatch(resourcePath, baseName, bundleNames,
										baseFileName, name);
							}
						}
					}
				}
			} catch (Exception e) {
				throw new RuntimeException(e.getMessage(), e);
			}
			return bundleNames;
		}

		private String getResourcePath() {
			String result = "";
			if (packageName != null) {
				result = packageName.replaceAll("\\.", "/") + "/";
			}
			return result;
		}

		private void addMatch(String resourcePath, String baseName,
				List<String> bundleNames, String baseFileName, String name) {
			int prefixed = name.indexOf(baseName);
			if (prefixed > -1 && name.endsWith(baseFileName)) {
				String toAdd = resourcePath + name.substring(0, prefixed)
						+ baseName;
				if (!bundleNames.contains(toAdd)) {
					bundleNames.add(toAdd);
				}
			}
		}

		static private class ResourceBundleEnumeration implements
				Enumeration<String> {

			private Set<String> set;
			private Iterator<String> iterator;
			private Enumeration<String> enumeration;
			private String next = null;

			ResourceBundleEnumeration(Set<String> set,
					Enumeration<String> enumeration) {
				this.set = set;
				this.iterator = set.iterator();
				this.enumeration = enumeration;
			}

			public boolean hasMoreElements() {
				if (next == null) {
					if (iterator.hasNext()) {
						next = iterator.next();
					} else if (enumeration != null) {
						while (next == null && enumeration.hasMoreElements()) {
							next = enumeration.nextElement();
							if (set.contains(next)) {
								next = null;
							}
						}
					}
				}
				return next != null;
			}

			public String nextElement() {
				if (hasMoreElements()) {
					String result = next;
					next = null;
					return result;
				} else {
					throw new NoSuchElementException();
				}
			}
		}
	}

}
