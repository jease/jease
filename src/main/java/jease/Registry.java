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

import com.thoughtworks.xstream.XStream;
import jease.cmf.domain.Node;
import jease.cmf.web.node.NodeEditor;
import jease.cms.domain.Content;
import jease.cms.domain.Parameter;
import jease.cms.domain.property.Property;
import jease.cms.web.content.editor.property.PropertyEditor;
import jfix.db4o.Database;
import jfix.util.Reflections;

import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.util.function.Supplier;

/**
 * Service to load component definitions for the CMS (domain class,
 * corresponding editor, [view], [icon]) from XML-file.
 * 
 * The service is able to process several XML files stored in a dedicated
 * location (META-INF/jease/registry.xml), so bundling classes with an
 * appropriate XML-file into a single jar allows to create drop in modules for
 * Jease.
 * 
 * The Registry provides also access to central parameters. Parameters are
 * simply key/value-pairs of strings which are stored in the database.
 */
public class Registry {

	private static class ParameterMap implements Supplier<Map<String, Parameter>> {
		public Map<String, Parameter> get() {
			Map<String, Parameter> map = new HashMap<>();
			for (Parameter parameter : Database.query(Parameter.class)) {
				map.put(parameter.getKey(), parameter);
			}
			return map;
		}
	}

	private static class Component {
		String domain, editor, icon, view;
	}

	private static Supplier<Map<String, Parameter>> parameters = new ParameterMap();
	private static Map<String, Component> components = new HashMap<>();
	private static Content[] contents;
	private static Property[] properties;

	static {
		try {
			init("META-INF/jease/registry.xml");
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage(), e);
		}
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
				components.put(component.domain, component);
			}
			url.close();
		}
		initDomainTypes(components.keySet());
	}

	private static void initDomainTypes(Set<String> domainClasses) {
		List<Content> contentList = new ArrayList<>();
		List<Property> propertyList = new ArrayList<>();

		for (String domainClass : domainClasses) {
			Object obj = Reflections.newInstance(domainClass);
			if (Content.class.isInstance(obj)) {
				contentList.add((Content) obj);
			}
			if (Property.class.isInstance(obj)) {
				propertyList.add((Property) obj);
			}
		}

		contentList.sort(Comparator.comparing(Content::getType));
		propertyList.sort(Comparator.comparing(Property::getType));

		contents = contentList.toArray(new Content[contentList.size()]);
		properties = propertyList.toArray(new Property[propertyList.size()]);
	}

	private static Component getComponent(Object obj) {
		return components.get(obj.getClass().getName());
	}

	private static Object newEditor(Object obj) {
		Component component = getComponent(obj);
		if (component != null && component.editor != null) {
			return Reflections.newInstance(component.editor);
		}
		return null;
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
		Component component = getComponent(node);
		return component != null ? component.icon : null;
	}

	/**
	 * Returns a view for given node.
	 */
	public static String getView(Node node) {
		Component component = getComponent(node);
		return component != null ? component.view : null;
	}

	/**
	 * Returns an editor for given node.
	 */
	public static NodeEditor<Node> getEditor(Node node) {
		return (NodeEditor<Node>) newEditor(node);
	}

	/**
	 * Returns an editor for given property.
	 */
	public static PropertyEditor<Property> getEditor(Property property) {
		return (PropertyEditor<Property>) newEditor(property);
	}

	/**
	 * Returns value of parameter for given key or null if key doesn't exist.
	 */
	public static String getParameter(String key) {
		Parameter parameter = Database.query(parameters).get(key);
		return parameter != null ? parameter.getValue() : null;
	}

	/**
	 * Returns value of parameter for given key or given default value if key
	 * doesn't exist.
	 */
	public static String getParameter(String key, String defaultValue) {
		String value = getParameter(key);
		return value != null ? value : defaultValue;
	}
}
