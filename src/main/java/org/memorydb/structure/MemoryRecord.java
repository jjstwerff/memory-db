package org.memorydb.structure;

import java.io.IOException;

import org.memorydb.file.Write;

public interface MemoryRecord {
	int getRec();

	default int getArrayIndex() {
		return -1;
	}

	void setRec(int rec);

	Store getStore();

	void output(Write write, int iterate) throws IOException;

	/**
	 * @throws IOException  
	 */
	default String keys() throws IOException {
		return "";
	}
}
