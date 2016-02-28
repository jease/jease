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
package jfix.db4o.xstream.converter;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.collections.ArrayConverter;
import com.thoughtworks.xstream.core.util.HierarchicalStreams;
import com.thoughtworks.xstream.io.ExtendedHierarchicalStreamWriterHelper;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.mapper.Mapper;

public class DelegateArrayConverter extends ArrayConverter {

	private Converter delegateConverter;

	public DelegateArrayConverter(Converter delegateConverter, Mapper mapper) {
		super(mapper);
		this.delegateConverter = delegateConverter;
	}

	protected void writeItem(Object item, MarshallingContext context,
			HierarchicalStreamWriter writer) {
		if (item == null) {
			String name = mapper().serializedClass(null);
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name,
					Mapper.Null.class);
			writer.endNode();
		} else {
			String name = mapper().serializedClass(item.getClass());
			ExtendedHierarchicalStreamWriterHelper.startNode(writer, name,
					item.getClass());
			context.convertAnother(item, delegateConverter);
			writer.endNode();
		}
	}

	protected Object readItem(HierarchicalStreamReader reader,
			UnmarshallingContext context, Object current) {
		Class<?> type = HierarchicalStreams.readClassType(reader, mapper());
		return context.convertAnother(current, type, delegateConverter);
	}

}
