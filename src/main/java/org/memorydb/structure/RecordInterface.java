package org.memorydb.structure;

public interface RecordInterface {
	public enum FieldType {
		INTEGER, LONG, FLOAT, STRING, DATE, BOOLEAN, // single types
		ITERATE, // this field holds a list of records
		OBJECT, // this field holds a new object
		FILTERS, // the next keys on the iterator can be filters
		SCHEMA // the filter can be schema filters.. they will not be individually listed
	}

	default RecordInterface getUpRecord() {
		return null;
	}

	/**
	 * return the next field defined on this record:
	 * -2 means unknown field
	 * -1 means end of fields found
	 *  0 is a special value.. this is not an object but a single value
	 * >0 is a field pointer
	 */
	default int next(int field) {
		int res = field < 0 ? 1 : field + 1;
		if (type(res) == null)
			return -2;
		return res;
	}

	String name(int field);

	default int scanName(String search) {
		if (search == null)
			return -1;
		for (int p = next(-1); p > 0; p = next(p)) {
			String n = name(p);
			if (n == null)
				return -1;
			if (search.equals(n))
				return p;
		}
		return -1;
	}

	default FieldType type() {
		return null;
	}

	/**
	 * Return null when an unknown field is requested. 
	 */
	FieldType type(int field);

	Object get(int field);

	/**
	 * @param field  
	 * @param key 
	 */
	default String key(int field, int key) {
		return null;
	}

	/**
	 * Return the possible type of restrictions on the iterator.
	 * When a key FILTERS is found the rest of the keys will loop the known iterators.
	 * @param field 
	 * @param key 
	 */
	default FieldType type(int field, int key) {
		return null;
	}

	/**
	 * @param name  
	 * @param min 
	 * @param max 
	 */
	default Object rangeFilter(String name, Object min, Object max) {
		return null;
	}

	/**
	 * @param name  
	 * @param inverse 
	 * @param values 
	 */
	default Object codedFilter(String name, boolean inverse, Object... values) {
		return null;
	}

	Iterable<? extends RecordInterface> iterate(int field, Object... key);

	default ChangeInterface change() {
		return null;
	}

	default int getSize() {
		return 0;
	}

	boolean exists();
}
