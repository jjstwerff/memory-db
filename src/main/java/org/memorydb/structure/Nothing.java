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
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		return null;
	}

	@Override
	public boolean next() {
		return false;
	}

	@Override
	public RecordInterface getClone() {
		return this;
	}

	@Override
	public RecordInterface get(String search) {
		return null;
	}

	@Override
	public RecordInterface get(int index) {
		return null;
	}
}
