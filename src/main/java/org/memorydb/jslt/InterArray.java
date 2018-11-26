package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterArray implements RecordInterface {
	private final JsltInterpreter interpreter;
	private final ArrayArray data;
	private int lastField;
	private Object lastObject;

	public InterArray(JsltInterpreter interpreter, ArrayArray data) {
		this.interpreter = interpreter;
		this.data = data;
		this.lastField = -1;
		this.lastObject = null;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public int getSize() {
		return data.getSize();
	}

	@Override
	public FieldType type(int field) {
		if (field < 1 || field > data.getSize())
			return null;
		if (lastField != field) {
			lastField = field;
			lastObject = interpreter.inter(new ArrayArray(data, field - 1));
		}
		return interpreter.type(lastObject);
	}

	@Override
	public Object get(int field) {
		if (lastField != field) {
			lastField = field;
			lastObject = interpreter.inter(new ArrayArray(data, field - 1));
		}
		return lastObject;
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return data.iterate(field, key);
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		int pos = 1;
		while (type(pos) != null) {
			if (pos != 1)
				bld.append(",");
			bld.append(get(pos));
			pos = next(pos);
		}
		bld.append("]");
		return bld.toString();
	}
}
