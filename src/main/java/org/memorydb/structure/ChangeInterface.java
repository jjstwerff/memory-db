package org.memorydb.structure;

public interface ChangeInterface extends RecordInterface, MemoryRecord, AutoCloseable {
	boolean set(int field, Object val);

	ChangeInterface add(int field);

	@Override
	default void close() {
		// nothing
	}
}
