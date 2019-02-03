package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterSlice implements RecordInterface {
	private RecordInterface data;
	private long[] parms;

	public InterSlice(JsltInterpreter interpreter, RecordInterface data, CallParmsArray parms) {
		this.data = data;
		this.parms = new long[parms.getSize() - 1];
		for (int p = 0; p < parms.getSize() - 1; p++)
			this.parms[p] = interpreter.getNumber(interpreter.inter(new CallParmsArray(parms, p + 1)));
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		int size = data.getSize();
		int idx = calc(field, size);
		if (idx < 0 || idx > size)
			return null;
		return JsltInterpreter.type(data.get(idx + 1));
	}

	private int calc(int field, int size) {
		for (int p = 0; p < parms.length; p += 3) {
			long start = this.parms[p];
			long stop = this.parms[p + 1];
			long step = this.parms[p + 2];
			if (step == Long.MIN_VALUE)
				step = 1;
			if (step < 0) {
				if (start == Long.MIN_VALUE)
					start = size - 1;
				if (start < 0)
					start += size;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = -1;
				if (start + step * (field - 1) > stop)
					return (int) (start + step * (field - 1));
			} else {
				if (start == Long.MIN_VALUE)
					start = 0;
				else if (start < 0)
					start += size;
				if (stop == Long.MAX_VALUE)
					stop = start + 1;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = size;
				if (start + step * (field - 1) < stop)
					return (int) (start + step * (field - 1));
			}
			long skip = (stop - start) / step;
			field -= skip;
		}
		return -1;
	}

	@Override
	public Object get(int field) {
		int size = data.getSize();
		int idx = calc(field, size);
		if (idx < 0 || idx > size)
			return null;
		return data.get(idx + 1);
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int getSize() {
		int size = data.getSize();
		int result = 0;
		for (int p = 0; p < parms.length; p += 3) {
			long start = this.parms[p];
			long stop = this.parms[p + 1];
			long step = this.parms[p + 2];
			if (step == Long.MIN_VALUE)
				step = 1;
			if (step < 0) {
				if (start == Long.MIN_VALUE)
					start = size - 1;
				if (start < 0)
					start += size;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = -1;
				result += (stop - start) / step;
			} else {
				if (start == Long.MIN_VALUE)
					start = 0;
				else if (start < 0)
					start += size;
				if (stop == Long.MAX_VALUE)
					stop = start + 1;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = size;
				result -= (start - stop) / step;
			}
		}
		return result;
	}

	@Override
	public FieldType type() {
		return FieldType.ARRAY;
	}
}
