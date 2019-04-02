package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Variable
 */
public class ChangeVariable extends Variable implements ChangeResultType {
	public ChangeVariable(Store store) {
		super(store, store.allocate(Variable.RECORD_SIZE));
		setName(null);
		setNr(0);
		setMultiple(false);
		defaultResultType();
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

	public void setMultiple(boolean value) {
		store.setByte(rec, 12, (store.getByte(rec, 12) & 254) + (value ? 1 : 0));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
		if (parser.hasField("nr")) {
			setNr(parser.getInt("nr"));
		}
		if (parser.hasField("multiple")) {
			Boolean valueMultiple = parser.getBoolean("multiple");
			if (valueMultiple == null)
				throw new MutationException("Mandatory 'multiple' field");
			setMultiple(valueMultiple);
		}
		parseResultType(parser);
	}

	@Override
	public void close() {
		// nothing yet
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 3 && field <= 5)
			return ChangeResultType.super.setResultType(field - 3, value);
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof Integer)
				setNr((Integer) value);
			return value instanceof Integer;
		case 3:
			if (value instanceof Boolean)
				setMultiple((Boolean) value);
			return value instanceof Boolean;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		if (field > 3 && field <= 5)
			return ChangeResultType.super.addResultType(field - 3);
		switch (field) {
		default:
			return null;
		}
	}
}