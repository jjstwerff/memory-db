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
	private final int field;

	public InterSlice(JsltInterpreter interpreter, RecordInterface data, CallParmsArray parms) {
		this.data = data;
		this.parms = new long[parms.size() - 1];
		this.cur = null;
		for (int p = 0; p < parms.size() - 1; p++)
			this.parms[p] = interpreter.getNumber(interpreter.inter(new CallParmsArray(parms, p + 1)));
		this.field = -1;
	}

	private InterSlice(RecordInterface data, long[] parms, int field) {
		this.data = data;
		this.parms = parms;
		this.field = field;
		int calc = calc(data.size());
		this.cur = calc < 0 ? null : data.index(calc);
	}

	@Override
	public String name() {
		return cur == null ? data.name() : cur.name();
	}

	@Override
	public FieldType type() {
		return field < 0 ? data.type() : cur.type();
	}

	@Override
	public Object java() {
		return cur == null ? data.java() : cur.java();
	}

	@Override
	public InterSlice index(int idx) {
		InterSlice res = new InterSlice(data, parms, idx);
		return res.cur == null ? null : res;
	}

	@Override
	public InterSlice start() {
		InterSlice res = new InterSlice(data, parms, 0);
		return res.cur == null ? null : res;
	}

	@Override
	public InterSlice next() {
		InterSlice res = new InterSlice(data, parms, field + 1);
		return res.cur == null ? null : res;
	}

	private int calc(int size) {
		int f = field;
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
				if (start + step * f > stop)
					return (int) (start + step * f);
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
				if (start + step * f < stop)
					return (int) (start + step * f);
			}
			long skip = (stop - start) / step;
			f -= skip;
		}
		return -1;
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
		return new InterSlice(data, parms, field);
	}

	@Override
	public String toString() {
		if (field >= 0)
			return cur == null ? null : cur.toString();
		StringBuilder bld = new StringBuilder();
		bld.append("[");
		InterSlice elm = start();
		int pos = 0;
		while (elm != null) {
			if (pos++ != 0)
				bld.append(",");
			bld.append(elm.java());
			elm = elm.next();
		}
		bld.append("]");
		return bld.toString();
	}
}
