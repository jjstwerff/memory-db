package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.List;

import org.memorydb.structure.RecordInterface;

public class ListArray implements RecordInterface {
	private List<Object> list = new ArrayList<>();

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (field <= 0 || field > list.size())
			return null;
		return JsltInterpreter.type(list.get(field - 1));
	}

	@Override
	public Object get(int field) {
		return list.get(field - 1);
	}

	public void add(Object elm) {
		list.add(elm);
	}

	@Override
	public String toString() {
		return list.toString();
	}
}
