package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

/**
 * TODO:
 * - start: skip to first needed element
 * - next: if not after last step over 'n' elements
 * -       after last.. go to next parms set
 */
public class InterSlice implements RecordInterface {
	private final RecordInterface data;
	private final RecordInterface cur;
	private final long[] parms;

	public InterSlice(JsltInterpreter interpreter, RecordInterface data, CallParmsArray parms) {
		this.data = data;
		this.parms = new long[parms.size() - 1];
		this.cur = null;
		for (int p = 0; p < parms.size() - 1; p++)
			this.parms[p] = interpreter.getNumber(interpreter.inter(new CallParmsArray(parms, p + 1)));
	}

	public InterSlice(RecordInterface data, RecordInterface cur, long[] parms) {
		this.data = data;
		this.cur = cur;
		this.parms = parms;
	}

	@Override
	public String name() {
		return cur == null ? data.name() : cur.name();
	}

	@Override
	public FieldType type() {
		return cur == null ? data.type() : cur.type();
	}

	@Override
	public InterSlice start() {
		return null;
	}

	@Override
	public InterSlice next() {
		return null;
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
	public Object java() {
		return data.java();
	}

	@Override
	public int size() {
		int size = data.size();
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
	public RecordInterface copy() {
		return new InterSlice(data, cur, parms);
	}
}
