package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class AddArray implements RecordInterface {
	private final RecordInterface array;
	private final Object elm;
	private final boolean structure;

	public AddArray(RecordInterface array, Object elm, boolean structure) {
		this.array = array;
		this.elm = elm;
		this.structure = structure;
	}

	@Override
	public AddArray start() {
		RecordInterface start = array == null ? null : array.start();
		if (start != null)
			return new AddArray(start, elm, structure);
		if (structure) {
			RecordInterface rest = (RecordInterface) elm;
			return new AddArray(rest.start(), null, false);
		}
		return new AddArray(null, elm, false);
	}

	@Override
	public AddArray next() {
		if (array == null)
			return null;
		RecordInterface next = array.next();
		if (next == null) {
			if (elm == null)
				return null;
			if (structure) {
				RecordInterface rest = (RecordInterface) elm;
				return new AddArray(rest.start(), null, false);
			}
			return new AddArray(null, elm, false);
		}
		return new AddArray(next, elm, structure);
	}

	@Override
	public String name() {
		return array == null ? null : array.name();
	}

	@Override
	public FieldType type() {
		return array == null ? JsltInterpreter.type(elm) : array.type();
	}

	@Override
	public int size() {
		if (array != null) {
			if (structure)
				return array.size() + ((RecordInterface) elm).size();
			return array.size() + (elm == null ? 0 : 1);
		}
		if (structure)
			return ((RecordInterface) elm).size();
		return elm == null ? 0 : 1;
	}

	@Override
	public Object java() {
		return array == null ? elm : array.java();
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		RecordInterface rec = array.start();
		while (rec != null) {
			bld.append(rec.java());
			rec = rec.next();
			bld.append(",");
		}
		bld.append(elm);
		bld.append("]");
		return bld.toString();
	}

	@Override
	public RecordInterface index(int idx) {
		int aSize = array != null ? array.size() : 0; 
		if (idx >= aSize) {
			if (structure)
				return ((RecordInterface) elm).index(idx - aSize);
			return new AddArray(null, elm, false);
		}
		return array.index(idx);
	}

	@Override
	public RecordInterface copy() {
		return new AddArray(array, elm, structure);
	}
}
