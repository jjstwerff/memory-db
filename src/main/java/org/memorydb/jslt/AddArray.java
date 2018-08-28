package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class AddArray implements RecordInterface {
	private final JsltInterpreter inter;
	private final RecordInterface array;
	private final Object elm;

	public AddArray(JsltInterpreter inter, RecordInterface array, Object elm) {
		this.inter = inter;
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
		int res = field < 0 ? 1: field + 1;
		if (type(res) == null)
			return -2;
		if (array instanceof InterObject) {
			InterObject ad = (InterObject) elm;
			while (res <= array.getSize()) {
				String n = name(res);
				boolean found = false;
				for (int f = ad.next(-1); f > -2; f = ad.next(f)) {
					if (ad.name(f).equals(n)) {
						found = true;
						break;
					}
				}
				if (!found)
					break;
				res++;
				if (type(res) == null)
					return -2;
			}
		}
		return res;
	}

	@Override
	public String name(int field) {
		int size = array.getSize();
		if (size < field)
			return ((RecordInterface) elm).name(field - size);
		return array.name(field);
	}

	@Override
	public FieldType type(int field) {
		int size = array.getSize();
		if (size < field) {
			if (elm instanceof RecordInterface)
				return ((RecordInterface) elm).type(field - size);
			else if (size + 1 == field)
				return inter.type(elm);
		}
		return array.type(field);
	}

	public int getSize() {
		return array.getSize() + (elm instanceof RecordInterface ? ((RecordInterface) elm).getSize() : 1);
	}

	@Override
	public Object get(int field) {
		int size = array.getSize();
		if (size < field) {
			if (elm instanceof RecordInterface)
				return ((RecordInterface) elm).get(field - size);
			else if (size + 1 == field)
				return elm;
		}
		return array.get(field);
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return null;
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
