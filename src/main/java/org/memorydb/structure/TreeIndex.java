package org.memorydb.structure;

public abstract class TreeIndex extends RedBlackTree implements RecordInterface {
	protected final Key key;
	protected final Store store;
	protected final int flag; // position of flag bit inside record
	protected final int field; // position of left/right field in bytes

	public TreeIndex(Store store, Key key, int flag, int field) {
		this.key = key;
		this.store = store;
		this.flag = flag;
		this.field = field;
	}

	public int search() {
		return key == null ? first() : find(key);
	}

	@Override
	protected boolean readRed(int recNr) {
		assert store.validate(recNr);
		return (store.getByte(recNr, flag >> 3) & (byte) (1 << (flag & 7))) > 0;
	}

	@Override
	protected void changeRed(int recNr, boolean value) {
		assert store.validate(recNr);
		int bitMap = 1 << (flag & 7);
		store.setByte(recNr, flag >> 3, (store.getByte(recNr, flag >> 3) & (255 - bitMap)) + (value ? bitMap : 0));
	}

	@Override
	protected int readLeft(int recNr) {
		assert store.validate(recNr);
		return store.getInt(recNr, field);
	}

	@Override
	protected void changeLeft(int recNr, int value) {
		assert store.validate(recNr);
		store.setInt(recNr, field, value);
	}

	@Override
	protected int readRight(int recNr) {
		assert store.validate(recNr);
		return store.getInt(recNr, field + 4);
	}

	@Override
	protected void changeRight(int recNr, int value) {
		assert store.validate(recNr);
		store.setInt(recNr, field + 4, value);
	}

	public int toNext(int fieldNr) {
		int result;
		if (fieldNr < 0)
			result = first();
		else
			result = next(fieldNr);
		return result == 0 ? -1 : result;
	}
}
