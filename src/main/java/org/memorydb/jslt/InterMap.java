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
	private Object lastValue;
	private final int index;

	public InterMap(JsltInterpreter interpreter, Expr expr, Object data, int index) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.lastValue = null;
		this.index = index;
	}

	@Override
	public RecordInterface start() {
		return step();
	}

	@Override
	public RecordInterface next() {
		return step();
	}

	private RecordInterface step() {
		if (data instanceof RecordInterface) {
			RecordInterface rec = (RecordInterface) data;
			RecordInterface next = index == 0 ? rec.start() : rec.next();
			fill(next == null, rec.java());
			return new InterMap(interpreter, expr, next, index + 1);
		} else if (data instanceof String) {
			String str = (String) data;
			fill(index >= str.length(), str.substring(index, index + 1));
			return new InterMap(interpreter, expr, str, index + 1);
		} else if (data instanceof Long) {
			Long val = (Long) data;
			fill(index >= val, index);
			return new InterMap(interpreter, expr, val, index + 1);
		}
		return null;
	}

	private void fill(boolean last, Object data) {
		interpreter.setIndex(index);
		interpreter.setFirst(index == 0);
		interpreter.setLast(last);
		if (data instanceof RecordInterface) {
			RecordInterface rec = (RecordInterface) data;
			interpreter.setCurrent(rec.java());
			interpreter.setCurName(rec.name());
		} else if (data instanceof String) {
			interpreter.setCurrent(((String) data));
			interpreter.setCurName(null);
		} else if (data instanceof Long) {
			interpreter.setCurrent(data);
			interpreter.setCurName(null);
		}
		lastValue = interpreter.inter(expr);
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		if (lastValue == null && index == 0)
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
