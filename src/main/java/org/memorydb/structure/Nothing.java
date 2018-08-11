package org.memorydb.structure;

import java.io.IOException;

import org.memorydb.file.Write;

// class to indicate no related type.
public class Nothing implements MemoryRecord, RecordInterface {
	@Override
	public int getRec() {
		return 0;
	}

	@Override
	public void setRec(int rec) {
		// nothing
	}

	@Override
	public Store getStore() {
		return null;
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		// nothing
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		return null;
	}

	@Override
	public Object get(int field) {
		return null;
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return null;
	}
}
