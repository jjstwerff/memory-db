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
	public int next(int field) {
		curField = field;
		boolean found = false;
		while(!found) {
			curField = data.next(curField);
			if (curField < 0)
				break;
			curType = data.type(curField);
			curValue = data.get(curField);
			interpreter.setCurrent(curValue);
			found = interpreter.getBoolean(interpreter.inter(expr));
		}
		return curField;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (field == curField)
			return curType;
		return data.type(field);
	}

	@Override
	public Object get(int field) {
		if (field == curField)
			return curValue;
		return data.get(field);
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
		return FieldType.ARRAY;
	}
}
