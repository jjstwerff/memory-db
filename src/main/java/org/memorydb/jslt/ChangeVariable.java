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
		setEager(false);
		setExtension(false);
	}

	public ChangeVariable(Variable current) {
		super(current.getStore(), current.getRec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setNr(int value) {
		if (value == Integer.MIN_VALUE)
			throw new MutationException("Mandatory 'nr' field");
		store.setInt(rec, 8, value);
	}

	public void setEager(boolean value) {
		store.setByte(rec, 12, (store.getByte(rec, 12) & 254) + (value ? 1 : 0));
	}

	public void setExtension(boolean value) {
		store.setByte(rec, 13, (store.getByte(rec, 13) & 254) + (value ? 1 : 0));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
		if (parser.hasField("nr")) {
			setNr(parser.getInt("nr"));
		}
		if (parser.hasField("eager")) {
			Boolean valueEager = parser.getBoolean("eager");
			if (valueEager == null)
				throw new MutationException("Mandatory 'eager' field");
			setEager(valueEager);
		}
		if (parser.hasField("extension")) {
			Boolean valueExtension = parser.getBoolean("extension");
			if (valueExtension == null)
				throw new MutationException("Mandatory 'extension' field");
			setExtension(valueExtension);
		}
		parseResultType(parser);
	}

	@Override
	public void close() {
		// nothing yet
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 4 && field <= 6)
			return ChangeResultType.super.setResultType(field - 4, value);
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
				setEager((Boolean) value);
			return value instanceof Boolean;
		case 4:
			if (value instanceof Boolean)
				setExtension((Boolean) value);
			return value instanceof Boolean;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		if (field >= 4 && field <= 6)
			return ChangeResultType.super.addResultType(field - 4);
		switch (field) {
		default:
			return null;
		}
	}
}