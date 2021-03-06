package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Macro
 */
public class ChangeMacro extends Macro implements AutoCloseable, ChangeInterface {
	public ChangeMacro(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Macro.RECORD_SIZE) : rec);
		if (rec == 0) {
			setName(null);
			store.setInt(rec(), 8, 0); // SET alternatives
			store.setInt(rec(), 12, 0); // ARRAY matching
			store.setInt(rec(), 16, 0);
		} else {
			new IndexMacros(store).remove(rec());
		}
	}

	public ChangeMacro(Macro current) {
		super(current.store(), current.rec());
		new IndexMacros(store).remove(rec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void moveMatching(ChangeMacro other) {
		store().setInt(rec(), 12, store().getInt(other.rec(), 12));
		store().setInt(other.rec(), 12, 0);
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("alternatives")) {
			Alternative.parse(parser, this);
		}
		if (parser.hasSub("matching")) {
			MatchingArray sub = new MatchingArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
	}

	@Override
	public void close() {
		new IndexMacros(store).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 2:
			return addAlternatives();
		case 3:
			return addMatching();
		default:
			return null;
		}
	}

	@Override
	public ChangeMacro copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeMacro(store, newRec);
	}
}
