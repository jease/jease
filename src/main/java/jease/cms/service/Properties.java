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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import jease.Registry;
import jease.cmf.domain.Node;
import jease.cmf.service.Nodes;
import jease.cms.domain.Content;
import jease.cms.domain.Factory;
import jease.cms.domain.property.Property;
import jease.cms.domain.property.Provider;
import jfix.db4o.Database;
import jfix.util.Natural;

/**
 * Service for handling properties.
 */
public class Properties {

	private static Supplier<String[]> propertyNames = () -> {
		Set<String> result = new HashSet<>();
		for (Property property : Database.query(Property.class)) {
			result.add(property.getName());
		}
		return Natural.sort(result.toArray(new String[] {}));
	};

	private static Supplier<String[]> providerPaths = () -> {
		final List<String> result = new ArrayList<>();
		Nodes.getRoot().traverse(new Consumer<Node>() {
			public void accept(Node node) {
				Content content = (Content) node;
				if (content.getProperties() != null) {
					for (Property property : content.getProperties()) {
						if (property instanceof Provider) {
							result.add(getPath(content, property));
						}
					}
				}
			}
		});
		return result.toArray(new String[] {});
	};

	/**
	 * Returns array of all available property types.
	 */
	public static Property[] getAvailableTypes() {
		return Registry.getProperties();
	}

	/**
	 * Returns all existing property names stored within database.
	 */
	public static String[] getPropertyNames() {
		return Database.query(propertyNames);
	}

	/**
	 * Returns all paths for properties which can act as item providers for
	 * selectable properties.
	 */
	public static String[] getProviderPaths() {
		return Database.query(providerPaths);
	}

	/**
	 * Returns path for given property contained in given content.
	 */
	public static String getPath(Content content, Property property) {
		String path = content.getPath();
		if (path.endsWith("/")) {
			return path + property.getName();
		} else {
			return content.getPath() + "/" + property.getName();
		}
	}

	/**
	 * Returns property for given path. If the path cannot be resolved, null is
	 * returned.
	 */
	public static Property getByPath(String path) {
		int idx = path.lastIndexOf("/");
		String contentPath = path.substring(0, idx);
		String propertyPath = path.substring(idx + 1);
		Content content = (Content) Nodes.getByPath(contentPath);
		if (content != null) {
			return content.getProperty(propertyPath);
		}
		return null;
	}

	/**
	 * Returns the corresponding property factory for given node in relation to
	 * given parent container. If no factory exists, null is returned.
	 */
	public static Factory getFactory(Content parent, Content content) {
		while (parent != null) {
			for (Factory factoryCandidate : parent.getChildren(Factory.class)) {
				for (Node prototype : factoryCandidate.getChildren()) {
					if (prototype.getClass().equals(content.getClass())) {
						return factoryCandidate;
					}
				}
			}
			parent = (Content) parent.getParent();
		}
		return null;
	}

	/**
	 * Returns factory-synchronized properties for given content in relation to
	 * given parent container.
	 */
	public static Property[] getProperties(Content parent, Content content) {
		Factory factory = getFactory(parent, content);
		if (factory != null && factory != parent) {
			return factory.getProperties(content);
		} else {
			return content.getProperties();
		}
	}
}
