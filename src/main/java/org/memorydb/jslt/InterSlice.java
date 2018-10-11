package org.memorydb.jslt;

import java.util.Arrays;

import org.memorydb.structure.RecordInterface;

public class InterSlice implements RecordInterface {
	private JsltInterpreter interpreter;
	private InterArray data;
	private long[] parms;
	private int size;

	public InterSlice(JsltInterpreter interpreter, CallParmsArray parms) {
		this.interpreter = interpreter;
		this.data = (InterArray) interpreter.inter(new CallParmsArray(parms, 0));
		this.size = data.getSize();
		this.parms = new long[parms.getSize() - 1];
		for (int p = 0; p < parms.getSize() - 1; p++)
			this.parms[p] = interpreter.getNumber(interpreter.inter(new CallParmsArray(parms, p + 1)));
		System.out.println(Arrays.toString(this.parms));
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		return interpreter.type(get(field));
	}

	private int calc(int field) {
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
			field -= skip;;
		}
		return -1;
	}

	@Override
	public Object get(int field) {
		int idx = calc(field);
		if (idx < 0 || idx > data.getSize())
			return null;
		return data.get(idx + 1);
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
		return FieldType.ITERATE;
	}
}
