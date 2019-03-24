package org.memorydb.structure;

public interface ChangeInterface extends RecordInterface, AutoCloseable {
	boolean java(Object val);

	/**
	 * Write a raw numeric value into this field
	 * @param val
	 */
	default boolean java(long val) {
		return true;
	}

	/**
	 * Add a new element to an array.
	 * @return
	 */
	default ChangeInterface add() {
		return null;
	}

	/**
	 * Add a new field to an object.
	 * @param field
	 */
	default ChangeInterface add(String field) {
		return null;
	}

	@Override
	default void close() {
		// nothing
	}
}
