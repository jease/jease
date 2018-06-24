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
package jease.cmf.service;

import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.util.Set;

import jease.cmf.annotation.NotSerialized;
import jease.cmf.domain.Node;
import jfix.db4o.Blob;
import jfix.db4o.Persistent;
import jfix.db4o.xstream.converter.BlobConverter;
import jfix.db4o.xstream.converter.DelegateArrayConverter;
import jfix.util.Reflections;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import com.thoughtworks.xstream.converters.SingleValueConverterWrapper;

public class Serializer {

	private XStream xstream;
	private Converter nodeConverter;
	private Converter nodeArrayConverter;

	public Serializer() {
		xstream = new XStream();
		xstream.registerConverter(new BlobConverter());
		nodeConverter = new SingleValueConverterWrapper(new NodeConverter());
		nodeArrayConverter = new DelegateArrayConverter(nodeConverter,
				xstream.getMapper());
	}

	public Set<Field> getFields(Node node) {
		return Reflections.getFields(node.getClass());
	}
	
	public void omitField(Field field) {
		xstream.omitField(field.getDeclaringClass(), field.getName());
	}

	public void registerConverter(Field field) {
		xstream.registerLocalConverter(field.getDeclaringClass(), field
				.getName(), field.getType().isArray() ? nodeArrayConverter
				: nodeConverter);
	}

	public boolean isBlob(Field field) {
		return Reflections.isAssignable(Blob.class, field);
	}

	public boolean isNode(Field field) {
		return Reflections.isAssignable(Node.class, field);
	}

	public boolean isPersistent(Field field) {
		return Reflections.isAssignable(Persistent.class, field);
	}

	public boolean isPersistentEntity(Field field) {
		return Reflections.isAssignable(Persistent.class, field)
				&& !(Reflections.isAssignable(Persistent.Value.class, field));
	}

	public boolean isPersistentValue(Field field) {
		return Reflections.isAssignable(Persistent.class, field)
				&& Reflections.isAssignable(Persistent.Value.class, field);
	}

	public boolean isNodeDeclaringClass(Field field) {
		return field.getDeclaringClass() == Node.class;
	}

	public boolean isNotSerialized(Field field) {
		return field.getAnnotation(NotSerialized.class) != null;
	}
	
	public void toXML(Node node, Writer writer) {
		xstream.toXML(node, writer);
	}

	public Node fromXML(Reader reader) {
		return (Node) xstream.fromXML(reader);
	}

	private static class NodeConverter implements SingleValueConverter {

		public boolean canConvert(Class clazz) {
			return Node.class.isAssignableFrom(clazz);
		}

		public String toString(Object obj) {
			return ((Node) obj).getPath();
		}

		public Object fromString(String str) {
			return Nodes.getByPath(str);
		}
	}
}
