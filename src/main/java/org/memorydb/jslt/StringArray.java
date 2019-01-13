package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class StringArray implements RecordInterface {
	private String string;

	public StringArray(String string) {
		this.string = string;
	}

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
		if (field <= 0 || field > string.length())
			return null;
		return FieldType.STRING;
	}

	@Override
	public Object get(int field) {
		return string.substring(field - 1, field);
	}

	@Override
	public boolean exists() {
		return string != null;
	}
}
