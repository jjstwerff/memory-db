package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Field
 */
public class ChangeField extends Field implements ChangePart {
	/* package private */ ChangeField(Part parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Field.RECORD_SIZE));
			defaultPart();
		}
		setName(null);
		setUpRecord(parent);
		if (rec != 0) {
			new Part.IndexObject(getUpRecord(), this).remove(rec);
		}
	}

	/* package private */ ChangeField(Field current) {
		super(current.store, current.rec);
		if (rec != 0) {
			new Part.IndexObject(getUpRecord(), this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setUpRecord(Part value) {
		store.setInt(rec, 17, value == null ? 0 : value.getRec());
		store.setInt(rec, 22, value == null ? 0 : value.getArrayIndex());
		byte type = 0;
		if (value instanceof Field)
			type = 1;
		if (value instanceof ArrayArray)
			type = 2;
		if (value instanceof Json)
			type = 3;
		store.setByte(rec, 21, type);
	}

	/* package private */ void parseFields(Parser parser) {
		parsePart(parser);
	}

	@Override
	public void close() {
		new Part.IndexObject(getUpRecord(), this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		if (field >= 2 && field <= 9)
			return ChangePart.super.setPart(field - 2, value);
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
		if (field >= 2 && field <= 9)
			return ChangePart.super.addPart(field - 2);
		switch (field) {
		default:
			return null;
		}
	}
}