package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class InterMult implements RecordInterface {
	private final RecordInterface data;
	private final RecordInterface cur;
	private final long number;

	public InterMult(RecordInterface data, RecordInterface cur, long number) {
		this.data = data;
		this.cur = cur;
		this.number = number;
	}

	@Override
	public InterMult start() {
		return new InterMult(data, data.start(), number);
	}

	@Override
	public InterMult next() {
		RecordInterface next = cur.next();
		if (next == null) {
			if (number == 0)
				return null;
			return new InterMult(data, data.start(), number - 1);
		}
		return new InterMult(data, next, number);
	}

	@Override
	public String name() {
		return cur.name();
	}

	@Override
	public FieldType type() {
		if (cur == null)
			return data.type();
		return cur.type();
	}

	@Override
	public Object java() {
		return cur == null ? null : cur.java();
	}

	@Override
	public int size() {
		if (cur == null) {
			if (data.type() == FieldType.ARRAY)
				return data.size() * (int) number;
			return (int) number;
		}
		return cur.size();
	}

	@Override
	public RecordInterface copy() {
		return new InterMult(data, cur, number);
	}
}
