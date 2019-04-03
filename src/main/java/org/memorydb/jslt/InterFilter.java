package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterFilter implements RecordInterface {

	private final JsltInterpreter interpreter;
	private final Expr expr;
	private final RecordInterface data;
	private Object curValue;
	private FieldType curType;
	private int curField;

	public InterFilter(JsltInterpreter interpreter, Expr expr, RecordInterface data) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.curType = null;
		this.curValue = null;
		this.curField = -1;
	}

	@Override
	public InterFilter next() {
		if (data == null)
			return null;
		boolean found = false;
		while (!found) {
			data.next();
			if (curField < 0)
				break;
			curType = data.type();
			curValue = data.java();
			interpreter.setCurrent(curValue);
			found = interpreter.getBoolean(interpreter.inter(expr));
		}
		return new InterFilter(interpreter, expr, data);
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		int field = 0;
		if (field == curField)
			return curType;
		return data.type();
	}

	@Override
	public Object java() {
		if (curValue != null)
			return curValue;
		return data.java();
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public RecordInterface copy() {
		return new InterFilter(interpreter, expr, data);
	}
}
