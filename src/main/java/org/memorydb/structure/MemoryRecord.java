package org.memorydb.structure;

import org.memorydb.file.Write;

public interface MemoryRecord {
	int rec();

	default int index() {
		return -1;
	}

	void rec(int rec);

	Store store();

	void output(Write write, int iterate);

	default String keys() {
		return "";
	}
}
