package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Listener
 */
public class ChangeListener extends Listener implements ChangeOperator {
	/* package private */ ChangeListener(Source parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Listener.RECORD_SIZE));
			defaultOperator();
		}
		setNr(0);
		store.setInt(getRec(), 8, 0); // SET levels
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexListeners(this).remove(rec);
		}
	}

	/* package private */ ChangeListener(Listener current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexListeners(this).remove(rec);
		}
	}

	public void setNr(int value) {
		store.setInt(rec, 4, value);
	}

	public void setUpRecord(Source value) {
		store.setInt(rec, 21, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("levels")) {
			new Level(store).parse(parser, this);
		}
		parseOperator(parser);
	}

	@Override
	public void close() {
		getUpRecord().new IndexListeners(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 3 && field <= 30)
			return ChangeOperator.super.setOperator(field - 3, value);
		switch (field) {
		case 1:
			if (value instanceof Integer)
				setNr((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 3 && field <= 30)
			return ChangeOperator.super.addOperator(field - 3);
		switch (field) {
		case 2:
			return addLevels();
		default:
			return null;
		}
	}
}