package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterArray implements RecordInterface {
	private JsltInterpreter interpreter;
	private ArrayArray data;

	public InterArray(JsltInterpreter interpreter, ArrayArray data) {
		this.interpreter = interpreter;
		this.data = data;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		return null;
	}

	@Override
	public Object get(int field) {
		return null;
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return null;
	}

	@Override
	public boolean exists() {
		return false;
	}
}
