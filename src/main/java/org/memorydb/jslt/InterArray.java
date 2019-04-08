package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterArray implements RecordInterface {
	private final JsltInterpreter interpreter;
	private final ArrayArray data;
	private int field;
	private Object lastObject;

	public InterArray(JsltInterpreter interpreter, ArrayArray data, int field) {
		this.interpreter = interpreter;
		this.data = data;
		this.field = field;
		this.lastObject = field < 0 || field >= data.size() ? null : interpreter.inter(data.index(field));
	}

	@Override
	public boolean testLast() {
		return field == data.size() - 1;
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public int index() {
		return field;
	}

	@Override
	public InterArray start() {
		if (data.size() == 0)
			return null;
		return new InterArray(interpreter, data, 0);
	}

	@Override
	public InterArray next() {
		if (field < 0 || field + 1 >= data.size())
			return null;
		return new InterArray(interpreter, data, field + 1);
	}

	@Override
	public FieldType type() {
		if (field < 0)
			return FieldType.ARRAY;
		return JsltInterpreter.type(lastObject);
	}

	@Override
	public Object java() {
		return lastObject;
	}

	@Override
	public InterArray index(int idx) {
		return idx >= 0 && idx < data.size() ? new InterArray(interpreter, data, idx) : null;
	}

	@Override
	public String toString() {
		if (field >= 0)
			return lastObject == null ? null : lastObject.toString();
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		InterArray elm = start();
		int pos = 0;
		while (elm != null) {
			if (pos++ != 0)
				bld.append(",");
			bld.append(elm.java());
			elm = elm.next();
		}
		bld.append("]");
		return bld.toString();
	}

	@Override
	public RecordInterface copy() {
		return new InterArray(interpreter, data, field);
	}
}
