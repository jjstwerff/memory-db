package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for table Field
 */
public class ChangeField extends Field implements AutoCloseable, ChangePart {
	/* package private */ ChangeField(Part parent, int rec) {
		super(parent.store(), rec == 0 ? parent.store().allocate(Field.RECORD_SIZE) : rec);
		if (rec == 0) {
			defaultPart();
			setName(null);
			up(parent);
		} else {
			new Part.IndexObject(this).remove(rec);
		}
	}

	/* package private */ ChangeField(Field current) {
		super(current.store, current.rec);
		if (rec != 0) {
			new Part.IndexObject(this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	private void up(Part value) {
		store.setInt(rec, 17, value == null ? 0 : value.rec());
		store.setInt(rec, 22, value == null ? 0 : value.index());
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
		new Part.IndexObject(this).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		if (field > 1 && field <= 8)
			return ChangePart.super.setPart(field - 1, value);
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
		if (field > 1 && field <= 8)
			return ChangePart.super.addPart(field - 1);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeField copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeField(up(), newRec);
	}
}
