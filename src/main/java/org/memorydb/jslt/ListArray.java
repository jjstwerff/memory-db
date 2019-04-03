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

	public ListArray(int field) {
		this.list = new ArrayList<>();
		this.field = field;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		return field < 0 ? FieldType.ARRAY : field > list.size() ? null : JsltInterpreter.type(list.get(field));
	}

	@Override
	public Object java() {
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
