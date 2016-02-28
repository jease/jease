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
package jease.cms.domain;

import java.util.Arrays;
import java.util.Date;
import java.util.UUID;

import jease.cmf.annotation.NotSerialized;
import jease.cmf.domain.Node;
import jease.cms.domain.property.Property;
import jfix.util.I18N;

import org.apache.commons.lang3.ArrayUtils;

/**
 * Abstract base class for building up a Content-Management-System.
 * 
 * This class stores a title, the date and the user of the last modification and
 * a flag which denotes if the content should be displayed in automatic
 * generated lists when displayed in the public site.
 * 
 * In order to revision content, an array of Versions is maintained where each
 * Version stores one revision of content. The newest revision is the first
 * entry of array.
 * 
 * In order to support adding attributes at runtime, the class can store an
 * array of properties.
 */
public abstract class Content extends Node {

	public interface PathChangeProcessor {
		void process(String oldPath, String newPath);
	}

	@NotSerialized
	private transient static PathChangeProcessor pathChangeProcessor;

	@NotSerialized
	private String uuid;
	private String title;
	private Date lastModified;
	private Date creationDate;
	private boolean visible;
	private Property[] properties;
	@NotSerialized
	private User editor;
	@NotSerialized
	private Version[] versions;

	public Content() {
		initUUID();
	}

	protected void initUUID() {
		if (uuid == null) {
			uuid = UUID.randomUUID().toString();
		}
	}

	public String getUUID() {
		return uuid;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * If true, the content should be visible in automatically created lists
	 * (e.g. navigations, search results).
	 */
	public boolean isVisible() {
		return visible;
	}

	public void setEditor(User editor) {
		this.editor = editor;
	}

	/**
	 * Returns the user who was the last one who edited the content.
	 */
	public User getEditor() {
		return editor;
	}

	/**
	 * Returns array of content revisions which are contained within Blobs.
	 */
	public Version[] getRevisions() {
		return versions;
	}

	/**
	 * Sets array of content revisions. The first element in the array should be
	 * the newest revision.
	 */
	public void setRevisions(Version[] versions) {
		this.versions = versions;
	}

	/**
	 * Adds a content revision contained in given Blob in first position of
	 * array, so newest revision is always first element in array.
	 */
	public void addRevision(Version version) {
		versions = ArrayUtils.add(versions, 0, version);
	}

	/**
	 * Returns an array of additional properties stored with content.
	 */
	public Property[] getProperties() {
		return properties;
	}

	/**
	 * Sets additional properties for content.
	 */
	public void setProperties(Property[] properties) {
		this.properties = properties;
	}

	/**
	 * Add given property to contet.
	 */
	public void addProperty(Property property) {
		properties = ArrayUtils.add(properties, property);
	}

	/**
	 * Returns all properties for given name.
	 */
	public Property[] getProperties(final String name) {
		return Arrays.stream(properties)
				.filter(property -> property != null && name.equals(property.getName()))
				.toArray(Property[]::new);
	}

	/**
	 * Returns first property with given name.
	 */
	public Property getProperty(String name) {
		if (properties != null) {
			for (Property property : properties) {
				if (property != null && name.equals(property.getName())) {
					return property;
				}
			}
		}
		return null;
	}

	/**
	 * Returns the value of the named property as string. If no property exists,
	 * the default value is returned.
	 */
	public String getProperty(String name, String defaultValue) {
		Property property = getProperty(name);
		return property != null ? String.valueOf(property) : defaultValue;
	}

	/**
	 * Returns localized type.
	 */
	public String getType() {
		return I18N.get(super.getType());
	}

	/**
	 * If true, the content-type is a container and can hold other nodes as
	 * children.
	 */
	public boolean isContainer() {
		return false;
	}

	/**
	 * Returns the size of the content in bytes. Derived implementations should
	 * add additional size information to super.getSize().
	 */
	public long getSize() {
		long size = super.getSize() + getTitle().length();
		if (properties != null) {
			for (Property property : properties) {
				if (property != null) {
					size += property.getSize();
				}
			}
		}
		return size;
	}

	/**
	 * Returns a fulltext-representation of the content as StringBuilder, so
	 * derived contents can append additional information directly to
	 * super.getFulltext() without the need to create new strings.
	 */
	public StringBuilder getFulltext() {
		StringBuilder sb = new StringBuilder(1024).append(getId()).append("\n")
				.append(getTitle()).append("\n").append(getType());
		if (properties != null) {
			for (Property property : properties) {
				if (property != null) {
					sb.append("\n").append(property.getName()).append("\n")
							.append(property.toString());
				}
			}
		}
		return sb;
	}

	/**
	 * If true, the content should rendered as page (e.g. with surrounding
	 * layout), otherwise the content is delivered directly.
	 */
	public boolean isPage() {
		return true;
	}

	/**
	 * Returns array of guard objects of given type in upstream direction by
	 * traversing children of parents for given content. In general a guard is a
	 * child in one of the parent folders of the given object which should have
	 * some influence on behaviour of the given object.
	 */
	public <E extends Content> E[] getGuards(Class<E> type) {
		Node currentParent = this;
		while (currentParent != null) {
			E[] guards = currentParent.getChildren(type);
			if (ArrayUtils.isNotEmpty(guards)) {
				return guards;
			}
			currentParent = currentParent.getParent();
		}
		return null;
	}

	/**
	 * Creates a controlled copy of the content. In derived implementations,
	 * super.copy() should be called and then additional fields should be
	 * copied. Elements of a container will copied automatically.
	 */
	public Content copy(boolean recursive) {
		Content content = (Content) super.copy(recursive);
		content.setTitle(getTitle());
		content.setLastModified(getLastModified());
		content.setEditor(getEditor());
		content.setVisible(isVisible());
		Property[] sourceProperties = getProperties();
		if (sourceProperties != null) {
			Property[] targetProperties = new Property[sourceProperties.length];
			for (int i = 0; i < sourceProperties.length; i++) {
				if (sourceProperties[i] != null) {
					targetProperties[i] = sourceProperties[i].copy();
				}
			}
			content.setProperties(targetProperties);
		}
		return content;
	}

	public void replace(String target, String replacement) {
		setId(getId().replace(target, replacement));
		setTitle(getTitle().replace(target, replacement));
		if (getProperties() != null) {
			for (Property property : getProperties()) {
				if (property != null) {
					property.replace(target, replacement);
				}
			}
		}
	}

	protected void onPathChange(String oldPath) {
		if (pathChangeProcessor != null) {
			pathChangeProcessor.process(oldPath, this.getPath());
		}
	}

	private Object readResolve() {
		initUUID();
		return this;
	}

	public static void setPathChangeProcessor(
			PathChangeProcessor pathChangeProcessor) {
		Content.pathChangeProcessor = pathChangeProcessor;
	}

}
