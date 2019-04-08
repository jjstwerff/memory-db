package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Json
 */
public class ChangeJson extends Json implements AutoCloseable, ChangePart {
	public ChangeJson(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Json.RECORD_SIZE) : rec);
		if (rec == 0) {
			defaultPart();
		} else {
		}
	}

	public ChangeJson(Json current) {
		super(current.store(), current.rec());
	}

	/* package private */ void parseFields(Parser parser) {
		parsePart(parser);
	}

	@Override
	public void close() {
		// nothing yet
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 0 && field <= 7)
			return ChangePart.super.setPart(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		if (field > 0 && field <= 7)
			return ChangePart.super.addPart(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeJson copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeJson(store, newRec);
	}
}
