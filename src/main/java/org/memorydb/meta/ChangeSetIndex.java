package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table SetIndex
 */
public class ChangeSetIndex extends SetIndex implements ChangeInterface {
	/* package private */ ChangeSetIndex(Record parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(SetIndex.RECORD_SIZE));
		}
		setIndex(null);
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexSetIndexes(this).remove(rec);
		}
	}

	/* package private */ ChangeSetIndex(SetIndex current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexSetIndexes(this).remove(rec);
		}
	}

	public void setIndex(Index value) {
		store.setInt(rec, 4, value == null ? 0 : value.getRec());
	}

	public void setUpRecord(Record value) {
		store.setInt(rec, 17, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(@SuppressWarnings("unused") Parser parser) {
		// empty
	}

	@Override
	public void close() {
		getUpRecord().new IndexSetIndexes(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Index)
				setIndex((Index) value);
			return value instanceof Index;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		default:
			return null;
		}
	}
}