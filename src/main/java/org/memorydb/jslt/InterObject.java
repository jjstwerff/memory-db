package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterObject implements RecordInterface {
	private JsltInterpreter interpreter;
	private ObjectArray data;

	public InterObject(JsltInterpreter interpreter, ObjectArray data) {
		this.interpreter = interpreter;
		this.data = data;
	}

	@Override
	public String name(int field) {
		return data.name(field);
	}

	@Override
	public FieldType type(int field) {
		return data.type(field);
	}

	@Override
	public Object get(int field) {
		Object object = data.get(field);
		if (object instanceof Operator) {
			Operator op = (Operator) object;
			switch (op.getOperation()) {
			case ARRAY:
				return new InterArray(interpreter, op.getArray());
			case BOOLEAN:
				return op.isBoolean();
			case FLOAT:
				return op.getFloat();
			case NULL:
				return null;
			case NUMBER:
				return op.getNumber();
			case OBJECT:
				return new InterObject(interpreter, op.getObject());
			case STRING:
				return op.getString();
			}
		}
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
