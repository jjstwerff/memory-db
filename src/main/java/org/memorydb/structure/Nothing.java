package org.memorydb.structure;

import java.util.Iterator;

import org.memorydb.file.Write;

// class to indicate no related type.
public class Nothing implements MemoryRecord, RecordInterface {
	@Override
	public int rec() {
		return 0;
	}

	@Override
	public Store store() {
		return null;
	}

	@Override
	public void output(Write write, int iterate) {
		// nothing
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		return null;
	}

	@Override
	public Nothing next() {
		return null;
	}

	@Override
	public RecordInterface copy() {
		return this;
	}

	@Override
	public Nothing copy(int rec) {
		return this;
	}
}
