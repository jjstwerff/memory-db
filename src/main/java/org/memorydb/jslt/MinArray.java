package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class MinArray implements RecordInterface {
	private final RecordInterface array;
	private final Object elm;

	public MinArray(RecordInterface array, Object elm) {
		this.array = array;
		this.elm = elm;
	}

	/**
	 * return the next field defined on this record: -2 means unknown field -1 means
	 * end of fields found 0 is a special value.. this is not an object but a single
	 * value >0 is a field pointer
	 */
	@Override
	public int next(int field) {
		int res = array.next(field);
		if (type(res) == null)
			return -2;
		if (array instanceof InterObject) {
			while (res != -2) {
				String n = name(res);
				if (!n.equals(elm))
					return res;
				res = array.next(res);
				if (type(res) == null)
					return -2;
			}
		}
		return res;
	}

	@Override
	public String name(int field) {
		return array.name(field);
	}

	@Override
	public FieldType type(int field) {
		return array.type(field);
	}

	@Override
	public int getSize() {
		return array.getSize(); // TODO correctly determine size
	}

	@Override
	public Object get(int field) {
		return array.get(field);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public FieldType type() {
		return array.type();
	}
}
