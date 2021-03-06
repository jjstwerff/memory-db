package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table MatchObject
 */
public class ChangeMatchObject extends MatchObject implements ChangeMatch {
	public ChangeMatchObject(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(MatchObject.RECORD_SIZE) : rec);
		if (rec == 0) {
			defaultMatch();
		} else {
		}
	}

	public ChangeMatchObject(MatchObject current) {
		super(current.store(), current.rec());
	}

	/* package private */ void parseFields(Parser parser) {
		parseMatch(parser);
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 0 && field <= 15)
			return ChangeMatch.super.setMatch(field - 0, value);
		switch (field) {
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		if (field > 0 && field <= 15)
			return ChangeMatch.super.addMatch(field - 0);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeMatchObject copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeMatchObject(store, newRec);
	}
}
