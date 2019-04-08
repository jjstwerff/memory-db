package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterFilter implements RecordInterface {

	private final JsltInterpreter interpreter;
	private final Expr expr;
	private final RecordInterface data;
	private RecordInterface curValue;

	public InterFilter(JsltInterpreter interpreter, Expr expr, RecordInterface data) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.curValue = null;
	}

	private InterFilter(JsltInterpreter interpreter, Expr expr, RecordInterface data, RecordInterface curValue) {
		this.interpreter = interpreter;
		this.expr = expr;
		this.data = data;
		this.curValue = curValue;
	}

	@Override
	public InterFilter start() {
		return curValue != null || data.size() == 0 ? null : next(data.start());
	}

	private InterFilter next(RecordInterface elm) {
		if (elm == null)
			return null;
		interpreter.setCurrent(elm.java());
		while (elm != null && !interpreter.getBoolean(interpreter.inter(expr))) {
			elm = elm.next();
			if (elm != null)
				interpreter.setCurrent(elm.java());
			else
				return null;
		}
		return new InterFilter(interpreter, expr, elm, elm);
	}

	@Override
	public InterFilter next() {
		return next(data.next());
	}

	@Override
	public String name() {
		return null;
	}

	@Override
	public FieldType type() {
		return curValue != null ? curValue.type() : FieldType.ARRAY;
	}

	@Override
	public Object java() {
		return curValue != null ? curValue.java() : null;
	}

	@Override
	public int size() { // TODO calculate including filters
		return data.size();
	}

	@Override
	public RecordInterface copy() {
		return new InterFilter(interpreter, expr, data);
	}
}
