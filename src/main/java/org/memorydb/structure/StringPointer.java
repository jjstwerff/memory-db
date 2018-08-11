package org.memorydb.structure;

public class StringPointer {
	private final int pos;
	private final int length;

	public StringPointer(int pos, int length) {
		this.pos = pos;
		this.length = length;
	}

	public int getPos() {
		return pos;
	}

	public int getLength() {
		return length;
	}
}
