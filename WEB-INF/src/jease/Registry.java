/*
    Copyright (C) 2010 maik.jablonski@gmail.com

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

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeEditor;
import jease.cms.domain.Parameter;
import jease.cms.domain.Content;
import jease.cms.domain.property.Property;
import jease.cms.web.content.editor.property.PropertyEditor;
import jfix.db4o.Database;
import jfix.functor.Supplier;
import jfix.util.Reflections;

import com.thoughtworks.xstream.XStream;

/**
 * Service to load component definitions for the CMS (domain class,
 * corresponding editor, [view], [icon]) from XML-file.
 * 
 * The service is able to process several XML files stored in a dedicated
 * location (META-INF/jease/registry.xml), so bundling classes with an
 * appropriate XML-file into a single jar allows to create drop in modules for
 * Jease.
 * 
 * The Registry provides also access to central parameters. Parameters
 * are simply key/value-pairs of strings which are stored in the database.
 */
public class Registry {

	private static class ParameterMap implements
			Supplier<Map<String, Parameter>> {
		public Map<String, Parameter> get() {
			Map<String, Parameter> map = new HashMap();
			for (Parameter parameter : Database.query(Parameter.class)) {
				map.put(parameter.getKey(), parameter);
			}
			return map;
		}
	}

	private static Supplier<Map<String, Parameter>> parameters = new ParameterMap();
	private static Map<String, String> icons = new HashMap();
	private static Map<String, String> editors = new HashMap();
	private static Map<String, String> views = new HashMap();
	private static Content[] contents;
	private static Property[] properties;

	static {
		try {
			init("META-INF/jease/registry.xml");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private static class Component {
		String domain, editor, icon, view;
	}

	private static void init(String resource) throws Exception {
		XStream xstream = new XStream();
		xstream.alias("jease", List.class);
		xstream.alias("component", Component.class);
		Enumeration<URL> urls = Registry.class.getClassLoader().getResources(
				resource);
		while (urls.hasMoreElements()) {
			InputStream url = urls.nextElement().openStream();
			for (Component component : (List<Component>) xstream.fromXML(url)) {
				registerComponent(component);
			}
			url.close();
		}
		initDomainTypes(editors.keySet());
	}

	private static void registerComponent(Component component) {
		editors.put(component.domain, component.editor);
		if (component.icon != null) {
			icons.put(component.domain, component.icon);
		}
		if (component.view != null) {
			views.put(component.domain, component.view);
		}
	}

	private static void initDomainTypes(Set<String> domainClasses) {
		List<Content> contentList = new ArrayList();
		List<Property> propertyList = new ArrayList();

		for (String domainClass : domainClasses) {
			Object obj = Reflections.newInstance(domainClass);
			if (Content.class.isInstance(obj)) {
				contentList.add((Content) obj);
			}
			if (Property.class.isInstance(obj)) {
				propertyList.add((Property) obj);
			}
		}

		Collections.sort(contentList, new Comparator<Content>() {
			public int compare(Content o1, Content o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});
		Collections.sort(propertyList, new Comparator<Property>() {
			public int compare(Property o1, Property o2) {
				return o1.getType().compareTo(o2.getType());
			}
		});

		contents = contentList.toArray(new Content[] {});
		properties = propertyList.toArray(new Property[] {});
	}

	/**
	 * Returns array of available content objects.
	 */
	public static Content[] getContents() {
		return contents;
	}

	/**
	 * Returns array of available property objects.
	 */
	public static Property[] getProperties() {
		return properties;
	}

	/**
	 * Returns an icon for given node.
	 */
	public static String getIcon(Node node) {
		return icons.get(node.getClass().getName());
	}

	/**
	 * Returns a view for given node.
	 */
	public static String getView(Node node) {
		return views.get(node.getClass().getName());
	}

	/**
	 * Returns an editor for given node.
	 */
	public static NodeEditor getEditor(Node node) {
		return (NodeEditor) Reflections.newInstance(editors.get(node.getClass()
				.getName()));
	}

	/**
	 * Returns an editor for given property.
	 */
	public static PropertyEditor getEditor(Property property) {
		return (PropertyEditor) Reflections.newInstance(editors.get(property
				.getClass().getName()));
	}

	/**
	 * Returns value of parameter for given key or null if key
	 * doesn't exist.
	 */
	public static String getParameter(String key) {
		Parameter parameter = Database.query(parameters).get(key);
		return parameter != null ? parameter.getValue() : null;
	}

}
