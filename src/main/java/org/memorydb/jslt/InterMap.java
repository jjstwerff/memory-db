package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterMap implements RecordInterface {

	private final JsltInterpreter interpreter;
	private final Expr expr;
	private final Object data;
	private int lastField;
	private Object lastValue;

	public InterMap(JsltInterpreter interpreter, Expr expr, Object data) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.lastField = -1;
		this.lastValue = null;
	}

	@Override
	public int next(int field) {
		if (data instanceof RecordInterface)
			return ((RecordInterface) data).next(field);
		if (data instanceof String) {
			field++;
			if (field >= ((String) data).length())
				return -2;
			interpreter.setIndex(field);
			interpreter.setFirst(field == 0);
			interpreter.setLast(field + 1 == ((String) data).length());
			return field;
		}
		if (data instanceof Long) {
			field++;
			if (field >= (Long) data)
				return -2;
			interpreter.setIndex(field);
			interpreter.setFirst(field == 0);
			interpreter.setLast(field + 1 == (Long) data);
			return field;
		}
		return -2;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (lastField != field)
			fill(field);
		return interpreter.type(lastValue);
	}

	@Override
	public Object get(int field) {
		if (lastField != field)
			fill(field);
		return lastValue;
	}

	private void fill(int field) {
		lastField = field;
		if (data instanceof RecordInterface) {
			RecordInterface rec = (RecordInterface) data;
			interpreter.setCurrent(rec.get(field));
			interpreter.setCurName(rec.name(field));
		} else if (data instanceof String) {
			interpreter.setCurrent(((String) data).substring(field, field + 1));
			interpreter.setCurName(null);
		} else if (data instanceof Long) {
			interpreter.setCurrent((long) field);
			interpreter.setCurName(null);
		}
		lastValue = interpreter.inter(expr);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getSize() {
		if (data instanceof RecordInterface)
			return ((RecordInterface) data).getSize();
		else if (data instanceof String)
			return ((String) data).length();
		else if (data instanceof Long)
			return (Integer) data;
		return -1;
	}

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}
}
