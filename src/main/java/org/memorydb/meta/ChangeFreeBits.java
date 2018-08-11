package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table FreeBits
 */
public class ChangeFreeBits extends FreeBits implements ChangeInterface {
	/* package private */ ChangeFreeBits(Record parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(FreeBits.RECORD_SIZE));
		}
		setSize(0);
		setPos(0);
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexFreeBits(this).remove(rec);
		}
	}

	/* package private */ ChangeFreeBits(FreeBits current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexFreeBits(this).remove(rec);
		}
	}

	public void setSize(int value) {
		store.setInt(rec, 4, value);
	}

	public void setPos(int value) {
		store.setInt(rec, 8, value);
	}

	public void setUpRecord(Record value) {
		store.setInt(rec, 21, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("pos")) {
			setPos(parser.getInt("pos"));
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexFreeBits(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Integer)
				setSize((Integer) value);
			return value instanceof Integer;
		case 2:
			if (value instanceof Integer)
				setPos((Integer) value);
			return value instanceof Integer;
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