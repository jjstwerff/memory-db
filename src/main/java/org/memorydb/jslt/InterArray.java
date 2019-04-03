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
	public String name() {
		return null;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (field < 1 || field > data.size())
			return null;
		if (lastField != field) {
			lastField = field;
			lastObject = interpreter.inter(new ArrayArray(data, field - 1));
		}
		return JsltInterpreter.type(lastObject);
	}

	@Override
	public Object java() {
		int field = 0;
		if (lastField != field) {
			lastField = field;
			lastObject = interpreter.inter(new ArrayArray(data, field - 1));
		}
		return lastObject;
	}

	@Override
	public String toString() {
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		int pos = 1;
		while (type() != null) {
			if (pos != 1)
				bld.append(",");
			bld.append(java());
			next();
		}
		bld.append("]");
		return bld.toString();
	}

	@Override
	public RecordInterface copy() {
		return new InterArray(interpreter, data);
	}
}
