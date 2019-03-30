package org.memorydb.structure;

public interface RecordInterface {
	public enum FieldType {
		INTEGER, LONG, FLOAT, STRING, DATE, BOOLEAN, // single types
		ARRAY, // this field holds a list of records
		OBJECT, // this field holds a new object
		NULL
	}

	/**
	 * Return the parent object of the current data.
	 */
	default RecordInterface up() {
		return null;
	}

	/**
	 * Get the corresponding java object of a field unless it is a structure (OBJECT / ARRAY) 
	 */
	default Object java() {
		return null;
	}

	/**
	 * Get the raw numeric content of a field, doesn't work for (OBJECT / ARRAY / NULL)
	 * On a STRING return the first character of Long.MIN_VALUE if the string is empty.
	 */
	default long number() {
		return Long.MIN_VALUE;
	}

	/**
	 * Return the first element of an ARRAY or the first field of an OBJECT.
	 * Return null when the ARRAY or OBJECT is empty of when the type is something else.
	 */
	default RecordInterface start() {
		return index(0);
	}

	/**
	 * Switch to the next field of an OBJECT or an element of a ARRAY.
	 * Return null if it was the last element.
	 */
	default RecordInterface next() {
		return null;
	}

	/**
	 * Return true if this is the last element on an ARRAY or field on an OBJECT.
	 */
	default boolean testLast() {
		return true;
	}

	/**
	 * Create a clone of the current element.
	 */
	RecordInterface copy();

	/**
	 * When this is a field of an OBJECT return its name.
	 * Otherwise returns null; 
	 */
	default String name() {
		return null;
	}

	/**
	 * When this is an OBJECT return the requested field.
	 * Otherwise return null.
	 * @param search the field name to search for.
	 */
	default RecordInterface field(String search) {
		return null;
	}

	/**
	 * When this is an ARRAY return the requested element.
	 * Otherwise return null.
	 * @param index the array position to jump to.
	 */
	default RecordInterface index(int index) {
		return null;
	}

	default FieldType type() {
		return null;
	}

	default ChangeInterface change() {
		return null;
	}

	/**
	 * Get the number of elements on an ARRAY or the number of fields on an OBJECT or the number of UTF characters on a STRING
	 */
	default int size() {
		return 0;
	}

	/**
	 * Test if the structure or STRING is empty.
	 */
	default boolean empty() {
		return true;
	}
}
