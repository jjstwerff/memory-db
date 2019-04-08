package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Variable
 */
public class ChangeVariable extends Variable implements ChangeResultType {
	public ChangeVariable(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Variable.RECORD_SIZE) : rec);
		if (rec == 0) {
			setName(null);
			setNr(Integer.MIN_VALUE);
			defaultResultType();
		} else {
		}
	}

	public ChangeVariable(Variable current) {
		super(current.store(), current.rec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setNr(int value) {
		store.setInt(rec, 8, value);
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
		if (parser.hasField("nr")) {
			setNr(parser.getInt("nr"));
		}
		parseResultType(parser);
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 2 && field <= 4)
			return ChangeResultType.super.setResultType(field - 2, value);
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof Integer)
				setNr((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		if (field > 2 && field <= 4)
			return ChangeResultType.super.addResultType(field - 2);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeVariable copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeVariable(store, newRec);
	}
}
