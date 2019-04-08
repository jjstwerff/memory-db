package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;

import org.memorydb.structure.RecordInterface;

public class ListArray implements RecordInterface {
	private final List<Object> list;
	private final int field;

	public ListArray(List<Object> list, int field) {
		this.list = list;
		this.field = field;
	}

	public ListArray() {
		this.list = new ArrayList<>();
		this.field = -1;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		return field < 0 ? FieldType.ARRAY : field >= list.size() ? null : JsltInterpreter.type(list.get(field));
	}

	@Override
	public RecordInterface start() {
		if (field < 0)
			return new ListArray(list, 0);
		Object o = list.get(field);
		if (o instanceof RecordInterface)
			return ((RecordInterface) o).start();
		return null;
	}

	@Override
	public RecordInterface next() {
		return field < 0 || field + 1 >= list.size() ? null : new ListArray(list, field + 1);
	}

	@Override
	public Object java() {
		if (field < 0 || field >= list.size())
			return null;
		return list.get(field);
	}

	public void add(Object elm) {
		list.add(elm);
	}

	@Override
	public String toString() {
		return list.toString();
	}

	@Override
	public ListArray copy() {
		return new ListArray(list, field);
	}
}
