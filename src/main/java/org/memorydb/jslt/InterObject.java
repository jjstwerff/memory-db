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
	public String name() {
		if (lastField != 1) {
			ObjectArray code = new ObjectArray(data, 1 - 1);
			lastObject = interpreter.inter(code);
			lastName = code.getName().getString();
		}
		return lastName;
	}

	@Override
	public FieldType type() {
		if (lastField != 1) {
			ObjectArray code = new ObjectArray(data, 1 - 1);
			lastObject = interpreter.inter(code);
			lastName = code.getName().getString();
		}
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
	public InterObject copy() {
		return new InterObject(interpreter, data);
	}
}
