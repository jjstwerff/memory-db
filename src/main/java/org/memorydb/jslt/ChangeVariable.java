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
	}

	public ChangeVariable(Variable current) {
		super(current.getStore(), current.getRec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("name")) {
			setName(parser.getString("name"));
		}
		parseResultType(parser);
	}

	@Override
	public void close() {
		// nothing yet
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 1 && field <= 3)
			return ChangeResultType.super.setResultType(field - 1, value);
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
	public ChangeInterface add(int field) {
		if (field >= 1 && field <= 3)
			return ChangeResultType.super.addResultType(field - 1);
		switch (field) {
		default:
			return null;
		}
	}
}