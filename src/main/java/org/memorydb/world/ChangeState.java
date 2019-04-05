package org.memorydb.world;

import org.memorydb.file.Parser;
import org.memorydb.structure.Store;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table State
 */
public class ChangeState extends State implements AutoCloseable, ChangeInterface {
	public ChangeState(Store store, int rec) {
		super(store, rec == 0 ? store.allocate(State.RECORD_SIZE) : rec);
		if (rec == 0) {
			setType(null);
			setLevel((byte) 0);
			setName(null);
		} else {
			new IndexRelations(store).remove(rec());
		}
	}

	public ChangeState(State current) {
		super(current.store(), current.rec());
		new IndexRelations(store).remove(rec());
	}

	public void setType(State.Type value) {
		if (value == null)
			throw new MutationException("Mandatory 'type' field");
		store.setByte(rec, 4, (store.getByte(rec, 4) & 224) + 1 + value.ordinal());
	}

	public void setLevel(byte value) {
		store.setByte(rec, 5, value);
	}

	public void setName(String value) {
		store.setInt(rec, 6, store.putString(value));
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field State.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasField("level")) {
			setLevel((byte) parser.getInt("level"));
		}
	}

	@Override
	public void close() {
		new IndexRelations(store).insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
		switch (field) {
		case 1:
			if (value instanceof State.Type)
				setType((State.Type) value);
			return value instanceof State.Type;
		case 2:
			if (value instanceof Byte)
				setLevel((Byte) value);
			return value instanceof Byte;
		case 3:
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
		default:
			return null;
		}
	}

	@Override
	public ChangeState copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeState(store, newRec);
	}
}
