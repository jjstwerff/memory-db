package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterObject implements RecordInterface {
	private JsltInterpreter interpreter;
	private ObjectArray data;
	private int lastField;
	private Object lastObject;
	private String lastName;

	public InterObject(JsltInterpreter interpreter, ObjectArray data) {
		this.interpreter = interpreter;
		this.data = data;
		this.lastField = -1;
		this.lastObject = null;
		this.lastName = null;
	}

	@Override
	public String name(int field) {
		if (lastField != field) {
			lastField = field;
			ObjectArray code = new ObjectArray(data, field - 1);
			lastObject = interpreter.inter(code);
			lastName = code.getName().getString();
		}
		return lastName;
	}

	@Override
	public FieldType type(int field) {
		if (field > data.getSize())
			return null;
		if (lastField != field) {
			lastField = field;
			ObjectArray code = new ObjectArray(data, field - 1);
			lastObject = interpreter.inter(code);
			lastName = code.getName().getString();
		}
		return interpreter.type(lastObject);
	}

	@Override
	public Object get(int field) {
		if (lastField != field) {
			lastField = field;
			ObjectArray code = new ObjectArray(data, field - 1);
			lastObject = interpreter.inter(code);
			lastName = code.getName().getString();
		}
		return lastObject;
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		return data.iterate(field, key);
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
		return FieldType.OBJECT;
	}
}
