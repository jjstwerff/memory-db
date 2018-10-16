package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterMult implements RecordInterface {
	private final JsltInterpreter interpreter;
	private final RecordInterface data;
	private final long number;

	public InterMult(JsltInterpreter interpreter, RecordInterface data, long number) {
		this.interpreter = interpreter;
		this.data = data;
		this.number = number;
	}

	@Override
	public int next(int field) {
		field++;
		if (data.type() == FieldType.ARRAY) {
			if (field >= number * data.getSize())
				return -2;
			return field;
		}
		if (field >= number)
			return -2;
		return field;
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		if (data.type() == FieldType.ARRAY) {
			if (field < 0 || field >= number * data.getSize())
				return null;
			int m = field % data.getSize();
			int pos = data.next(-1);
			for(int i=0; i<m; i++) {
				pos = data.next(pos);
			}
			return interpreter.type(data.get(pos));
		}
		if (field < 0 || field >= number)
			return null;
		return interpreter.type(data);
	}

	@Override
	public Object get(int field) {
		if (data.type() == FieldType.ARRAY) {
			if (field < 0 || field >= number * data.getSize())
				return null;
			int m = field % data.getSize();
			int pos = data.next(-1);
			for(int i=0; i<m; i++) {
				pos = data.next(pos);
			}
			return data.get(pos);
		}
		if (field < 0 || field >= number)
			return null;
		return data;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getSize() {
		if (data.type() == FieldType.ARRAY)
			return data.getSize() * (int) number;
		return (int) number;
	}

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}
}
