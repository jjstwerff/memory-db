package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Special
 */
public class ChangeSpecial extends Special implements AutoCloseable, ChangeInterface {
	public ChangeSpecial(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(Special.RECORD_SIZE) : rec);
		if (rec == 0) {
			setName(null);
			setOpposite(null);
			setDescription(null);
			setTaste(false);
		} else {
			new IndexSpecials(store).remove(rec());
		}
	}

	public ChangeSpecial(Special current) {
		super(current.store(), current.rec());
		new IndexSpecials(store).remove(rec());
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setOpposite(String value) {
		store.setInt(rec, 8, store.putString(value));
	}

	public void setDescription(String value) {
		store.setInt(rec, 12, store.putString(value));
	}

	public void setTaste(boolean value) {
		store.setByte(rec, 16, (store.getByte(rec, 16) & 254) + (value ? 1 : 0));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("opposite")) {
			setOpposite(parser.getString("opposite"));
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
		if (parser.hasField("taste")) {
			Boolean valueTaste = parser.getBoolean("taste");
			if (valueTaste == null)
				throw new MutationException("Mandatory 'taste' field");
			setTaste(valueTaste);
		}
	}

	@Override
	public void close() {
		new IndexSpecials(store).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof String)
				setOpposite((String) value);
			return value instanceof String;
		case 3:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		case 4:
			if (value instanceof Boolean)
				setTaste((Boolean) value);
			return value instanceof Boolean;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public ChangeSpecial copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeSpecial(store, newRec);
	}
}
