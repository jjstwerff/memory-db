package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

/**
 * Iterate along an Array or Object, a String or count numbers till a specific value.
 *
 * Set the following interpreter fields:
 * . setIndex() = the number of steps taken
 * . setFirst() = boolean if it is the first element
 * . setLast() = boolean if it is the last element
 * . setCurrent() = the java presentation of the current element
 * . setCurName() = the java presentation fo the current name
 */
public class InterMap implements RecordInterface {
	private final JsltInterpreter interpreter;
	private final Expr expr;
	private final Object data;
	private final Object lastValue;
	private final int index;

	public InterMap(JsltInterpreter interpreter, Expr expr, Object data) {
		this.interpreter = interpreter;
		this.expr = expr;
		if (data instanceof RecordInterface) {
			RecordInterface rec = (RecordInterface) data;
			this.data = rec.start();
		} else
			this.data = data;
		this.lastValue = null;
		this.index = -1;
	}

	private InterMap(JsltInterpreter interpreter, Expr expr, Object data, int index) {
		this.interpreter = interpreter;
		this.expr = expr;
		interpreter.setIndex(index);
		interpreter.setFirst(index == 0);
		if (data instanceof RecordInterface) {
			RecordInterface rec = (RecordInterface) data;
			RecordInterface next = rec.next();
			interpreter.setCurName(rec.name());
			interpreter.setLast(next == null);
			interpreter.setCurrent(rec.java());
			this.data = next;
		} else if (data instanceof String) {
			String str = (String) data;
			boolean last = index + 1 >= str.length();
			interpreter.setLast(last);
			interpreter.setCurrent(str.substring(index, index + 1));
			this.data = last ? null : data;
		} else if (data instanceof Long) {
			Long l = (Long) data;
			boolean last = index + 1 >= l;
			interpreter.setLast(last);
			interpreter.setCurrent((long) index);
			this.data = last ? null : data;
		} else
			this.data = null;
		this.lastValue = interpreter.inter(expr);
		this.index = index;
	}

	@Override
	public RecordInterface start() {
		return data == null ? null : new InterMap(interpreter, expr, data, 0);
	}

	@Override
	public RecordInterface next() {
		return data == null ? null : new InterMap(interpreter, expr, data, index + 1);
	}

	@Override
	public RecordInterface index(int idx) {
		return idx < 0 || idx >= size() ? null : new InterMap(interpreter, expr, data, index);
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		if (index < 0)
			return FieldType.ARRAY;
		return JsltInterpreter.type(lastValue);
	}

	@Override
	public Object java() {
		return lastValue;
	}

	@Override
	public int size() {
		if (data instanceof RecordInterface)
			return ((RecordInterface) data).size();
		else if (data instanceof String)
			return ((String) data).length();
		else if (data instanceof Long)
			return (Integer) data;
		return -1;
	}

	@Override
	public InterMap copy() {
		return new InterMap(interpreter, expr, data, index);
	}
}
