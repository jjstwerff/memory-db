package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;

public class InterMap implements RecordInterface {

	private final JsltInterpreter interpreter;
	private final Expr expr;
	private final RecordInterface data;
	private int lastField;
	private Object lastValue;

	public InterMap(JsltInterpreter interpreter, Expr expr, RecordInterface data) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.lastField = -1;
		this.lastValue = null;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (lastField != field) {
			lastField = field;
			interpreter.setCurrent(data.get(field));
			lastValue = interpreter.inter(expr);
		}
		return interpreter.type(lastValue);
	}

	@Override
	public Object get(int field) {
		if (lastField != field) {
			lastField = field;
			interpreter.setCurrent(data.get(field));
			lastValue = interpreter.inter(expr);
		}
		return lastValue;
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
	public int getSize() {
		return data.getSize();
	}

	@Override
	public FieldType type() {
		return FieldType.ITERATE;
	}
}
