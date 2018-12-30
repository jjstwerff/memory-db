package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Level
 */
public class ChangeLevel extends Level implements ChangeOperator {
	/* package private */ ChangeLevel(Listener parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Level.RECORD_SIZE));
			defaultOperator();
		}
		setLevel(0);
		store.setInt(getRec(), 8, 0); // ARRAY order
		store.setInt(getRec(), 12, 0); // ARRAY slice
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexLevels(this).remove(rec);
		}
	}

	/* package private */ ChangeLevel(Level current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexLevels(this).remove(rec);
		}
	}

	public void setLevel(int value) {
		store.setInt(rec, 4, value);
	}

	public void moveOrder(ChangeLevel other) {
		getStore().setInt(getRec(), 8, getStore().getInt(other.getRec(), 8));
		getStore().setInt(other.getRec(), 8, 0);
	}

	public void moveSlice(ChangeLevel other) {
		getStore().setInt(getRec(), 12, getStore().getInt(other.getRec(), 12));
		getStore().setInt(other.getRec(), 12, 0);
	}

	public void setUpRecord(Listener value) {
		store.setInt(rec, 25, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("order")) {
			try (OrderArray sub = new OrderArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("slice")) {
			try (SliceArray sub = new SliceArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		parseOperator(parser);
	}

	@Override
	public void close() {
		getUpRecord().new IndexLevels(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 4 && field <= 32)
			return ChangeOperator.super.setOperator(field - 4, value);
		switch (field) {
		case 1:
			if (value instanceof Integer)
				setLevel((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 4 && field <= 32)
			return ChangeOperator.super.addOperator(field - 4);
		switch (field) {
		case 2:
			return addOrder();
		case 3:
			return addSlice();
		default:
			return null;
		}
	}
}