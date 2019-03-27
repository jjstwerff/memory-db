package org.memorydb.structure;

import java.util.Iterator;

public interface RecordInterface extends Iterable<RecordInterface> {
	public enum FieldType {
		INTEGER, LONG, FLOAT, STRING, DATE, BOOLEAN, // single types
		ARRAY, // this field holds a list of records
		OBJECT, // this field holds a new object
		FILTERS, // the next keys on the iterator can be filters
		SCHEMA, // the filter can be schema filters.. they will not be individually listed
		NULL
	}

	/**
	 * Return the parent object of the current data.
	 */
	default RecordInterface getUpRecord() {
		return null;
	}

	default Object get() {
		return null;
	}

	/**
	 * Return the first element of an ARRAY or the first field of an OBJECT.
	 * Return null when the ARRAY or OBJECT is empty of when the type is something else.
	 */
	default RecordInterface start() {
		return get(0);
	}

	/**
	 * Switch to the next field of an OBJECT or an element of a ARRAY.
	 * Return false if it was the last element.
	 */
	boolean next();

	/**
	 * Return true if this is the last element on an ARRAY or field on an OBJET.
	 */
	default boolean isLast() {
		return true;
	}

	/**
	 * Create a clone of the current element.
	 */
	RecordInterface getClone();

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
	 */
	RecordInterface get(String search);

	/**
	 * When this is an ARRAY return the requested element.
	 * Otherwise return null.
	 */
	RecordInterface get(int index);

	default FieldType type() {
		return null;
	}

	/**
	 * Iterate through elements in an ARRAY or fields on an OBJECT
	 */
	default Iterator<RecordInterface> iterator() {
		start();
		return new Iterator<RecordInterface>() {
			@Override
			public boolean hasNext() {
				return !isLast();
			}

			@Override
			public RecordInterface next() {
				next();
				return RecordInterface.this;
			}
		};
	}

	default ChangeInterface change() {
		return null;
	}

	default int getSize() {
		return 0;
	}
}
