package org.memorydb.jslt;

import org.memorydb.structure.RecordInterface;

public class MinArray implements RecordInterface {
	private final RecordInterface array;
	private final Object elm;

	public MinArray(RecordInterface array, Object elm) {
		this.array = array;
		this.elm = elm;
	}

	@Override
	public MinArray start() {
		return getNext(array.start());
	}

	@Override
	public MinArray next() {
		return getNext(array.next());
	}

	private MinArray getNext(RecordInterface next) {
		while (next != null && next.name().equals(elm)) {
			next = next.next();
		}
		return next == null ? null : new MinArray(next, elm);
	}

	@Override
	public String name() {
		return array.name();
	}

	@Override
	public FieldType type() {
		return array.type();
	}

	@Override
	public int size() {
		return array.size(); // TODO correctly determine size
	}

	@Override
	public Object java() {
		return array.java();
	}

	@Override
	public MinArray copy() {
		return new MinArray(array, elm);
	}
}
