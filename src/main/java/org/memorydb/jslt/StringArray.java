package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class StringArray implements RecordInterface {
	private final String string;
	private final int pos;

	public StringArray(String string, int pos) {
		this.string = string;
		this.pos = pos;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public StringArray start() {
		return new StringArray(string, 0);
	}

	@Override
	public StringArray index(int idx) {
		return idx < 0 || idx >= string.length() ? null : new StringArray(string, idx);
	}

	@Override
	public StringArray next() {
		return pos + 1 >= string.length() ? null : new StringArray(string, pos + 1);
	}

	@Override
	public FieldType type() {
		return pos < 0 ? FieldType.ARRAY : FieldType.STRING;
	}

	@Override
	public Object java() {
		return string.substring(pos, pos + 1);
	}

	@Override
	public String toString() {
		return string;
	}

	@Override
	public StringArray copy() {
		return new StringArray(string, pos);
	}
}
