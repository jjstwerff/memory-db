package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterObject implements RecordInterface {
	private JsltInterpreter interpreter;
	private ObjectArray data;
	private int field;
	private Object lastObject;
	private String lastName;

	public InterObject(JsltInterpreter interpreter, ObjectArray data, int field) {
		this.interpreter = interpreter;
		this.data = data;
		this.field = field;
		ObjectArray obj = field < 0 || field >= data.size() ? null : data.index(field);
		if (obj == null) {
			lastObject = null;
			lastName = null;
		} else {
			lastObject = interpreter.inter(obj);
			lastName = interpreter.getString(interpreter.inter(obj.getName()));
		}
	}

	private InterObject(JsltInterpreter interpreter, ObjectArray data, String lastName) {
		this.interpreter = interpreter;
		this.data = data;
		this.field = Integer.MIN_VALUE;
		this.lastObject = interpreter.inter(data);
		this.lastName = lastName;
	}

	@Override
	public InterObject start() {
		if (data.size() == 0)
			return null;
		return new InterObject(interpreter, data, 0);
	}

	@Override
	public InterObject next() {
		if (field < 0 || field + 1 >= data.size())
			return null;
		return new InterObject(interpreter, data, field + 1);
	}

	@Override
	public InterObject field(String search) {
		ObjectArray obj = data.start();
		while (obj != null) {
			Object inter = interpreter.inter(obj.getName());
			if (inter instanceof String && inter.equals(search))
				return new InterObject(interpreter, obj, search);
			obj = obj.next();
		}
		return null;
	}

	@Override
	public String name() {
		return lastName;
	}

	@Override
	public FieldType type() {
		if (field < 0)
			return FieldType.OBJECT;
		return JsltInterpreter.type(lastObject);
	}

	@Override
	public Object java() {
		return lastObject;
	}

	@Override
	public int size() {
		return data.size();
	}

	@Override
	public String toString() {
		if (field >= 0)
			return lastObject == null ? null : lastObject.toString();
		StringBuilder bld = new StringBuilder();
		bld.append("{");
		InterObject elm = start();
		int pos = 0;
		while (elm != null) {
			if (pos++ != 0)
				bld.append(",");
			bld.append(elm.name()).append(":").append(elm.java());
			elm = elm.next();
		}
		bld.append("}");
		return bld.toString();
	}

	@Override
	public InterObject copy() {
		return new InterObject(interpreter, data, field);
	}
}
