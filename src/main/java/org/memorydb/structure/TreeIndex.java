package org.memorydb.structure;

import java.util.Iterator;
import java.util.NoSuchElementException;

public abstract class TreeIndex<T extends MemoryRecord> extends RedBlackTree implements Iterable<T>, RecordInterface {
	private final Key key;
	private final T record;
	private final int flag; // position of flag bit inside record
	private final int field; // position of left/right field in bytes

	public TreeIndex(T record, Key key, int flag, int field) {
		this.key = key;
		this.record = record;
		this.flag = flag;
		this.field = field;
	}

	@Override
	public Iterator<T> iterator() {
		return new Iterator<>() {
			int nextRec = search();

			@Override
			public boolean hasNext() {
				return nextRec != 0;
			}

			@Override
			public T next() {
				if (nextRec == 0)
					throw new NoSuchElementException();
				int n = nextRec;
				nextRec = TreeIndex.super.next(nextRec);
				if (key != null && key.compareTo(nextRec) != 0)
					nextRec = 0;
				record.setRec(n);
				return record;
			}
		};
	}

	public int search() {
		return key == null ? first() : find(key);
	}

	@Override
	protected boolean readRed(int recNr) {
		assert record.getStore().validate(recNr);
		return (record.getStore().getByte(recNr, flag >> 3) & (byte) (1 << (flag & 7))) > 0;
	}

	@Override
	protected void changeRed(int recNr, boolean value) {
		assert record.getStore().validate(recNr);
		int bitMap = 1 << (flag & 7);
		record.getStore().setByte(recNr, flag >> 3, (record.getStore().getByte(recNr, flag >> 3) & (255 - bitMap)) + (value ? bitMap : 0));
	}

	@Override
	protected int readLeft(int recNr) {
		assert record.getStore().validate(recNr);
		return record.getStore().getInt(recNr, field);
	}

	@Override
	protected void changeLeft(int recNr, int value) {
		assert record.getStore().validate(recNr);
		record.getStore().setInt(recNr, field, value);
	}

	@Override
	protected int readRight(int recNr) {
		assert record.getStore().validate(recNr);
		return record.getStore().getInt(recNr, field + 4);
	}

	@Override
	protected void changeRight(int recNr, int value) {
		assert record.getStore().validate(recNr);
		record.getStore().setInt(recNr, field + 4, value);
	}

	@Override
	public String name(int field) {
		return null;
	}

	@Override
	public FieldType type(int field) {
		return FieldType.OBJECT;
	}

	@Override
	public boolean exists() {
		return true;
	}

	@Override
	public int next(int field) {
		int result;
		if (field < 0)
			result = first();
		else
			result = super.next(field);
		return result == 0 ? -1 : result;
	}
}
