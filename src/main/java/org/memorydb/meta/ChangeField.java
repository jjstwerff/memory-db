package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import java.util.Iterator;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Field
 */
public class ChangeField extends Field implements ChangeInterface {
	/* package private */ ChangeField(Record parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Field.RECORD_SIZE));
		}
		setName(null);
		setType(Field.Type.STRING);
		setAuto(false);
		setPos(0);
		setIndex(null);
		store.setInt(getRec(), 18, 0); // ARRAY values
		setKey(false);
		setMandatory(false);
		setDefault(null);
		setDescription(null);
		setRelated(null);
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexFieldOnName(this).remove(rec);
		}
	}

	/* package private */ ChangeField(Field current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexFieldOnName(this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setType(Field.Type value) {
		if (value == null)
			throw new MutationException("Mandatory 'type' field");
		store.setByte(rec, 8, (store.getByte(rec, 8) & 224) + 1 + value.ordinal());
	}

	public void setAuto(boolean value) {
		store.setByte(rec, 9, (store.getByte(rec, 9) & 254) + (value ? 1 : 0));
	}

	public void setPos(int value) {
		if (value == Integer.MIN_VALUE)
			throw new MutationException("Mandatory 'pos' field");
		store.setInt(rec, 10, value);
	}

	public void setIndex(Index value) {
		store.setInt(rec, 14, value == null ? 0 : value.getRec());
	}

	public void moveValues(ChangeField other) {
		getStore().setInt(getRec(), 18, getStore().getInt(other.getRec(), 18));
		getStore().setInt(other.getRec(), 18, 0);
	}

	public void setKey(boolean value) {
		store.setByte(rec, 22, (store.getByte(rec, 22) & 254) + (value ? 1 : 0));
	}

	public void setMandatory(boolean value) {
		store.setByte(rec, 23, (store.getByte(rec, 23) & 254) + (value ? 1 : 0));
	}

	public void setDefault(String value) {
		store.setInt(rec, 24, store.putString(value));
	}

	public void setDescription(String value) {
		store.setInt(rec, 28, store.putString(value));
	}

	public void setRelated(Record value) {
		store.setInt(rec, 45, value == null ? 0 : value.getRec());
	}

	public void setUpRecord(Record value) {
		store.setInt(rec, 41, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field Field.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasField("auto")) {
			Boolean valueAuto = parser.getBoolean("auto");
			if (valueAuto == null)
				throw new MutationException("Mandatory 'auto' field");
			setAuto(valueAuto);
		}
		if (parser.hasField("pos")) {
			setPos(parser.getInt("pos"));
		}
		if (parser.hasField("index")) {
			parser.getRelation("index", (int recNr) -> {
				Iterator<Index> iterator = getUpRecord().getUpRecord().getIndexes().iterator();
				Index relRec = iterator.hasNext() ? iterator.next() : null;
				boolean found = relRec != null && relRec.parseKey(parser);
				setRec(recNr);
				setIndex(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasSub("values")) {
			try (ValuesArray sub = new ValuesArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("key")) {
			Boolean valueKey = parser.getBoolean("key");
			if (valueKey == null)
				throw new MutationException("Mandatory 'key' field");
			setKey(valueKey);
		}
		if (parser.hasField("mandatory")) {
			Boolean valueMandatory = parser.getBoolean("mandatory");
			if (valueMandatory == null)
				throw new MutationException("Mandatory 'mandatory' field");
			setMandatory(valueMandatory);
		}
		if (parser.hasField("default")) {
			setDefault(parser.getString("default"));
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
		if (parser.hasField("related")) {
			parser.getRelation("related", (int recNr) -> {
				Iterator<Record> iterator = getUpRecord().getUpRecord().getRecords().iterator();
				Record relRec = iterator.hasNext() ? iterator.next() : null;
				boolean found = relRec != null && relRec.parseKey(parser);
				setRec(recNr);
				setRelated(relRec);
				return found;
			}, getRec());
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexFieldOnName(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 2:
			if (value instanceof Field.Type)
				setType((Field.Type) value);
			return value instanceof Field.Type;
		case 3:
			if (value instanceof Boolean)
				setAuto((Boolean) value);
			return value instanceof Boolean;
		case 4:
			if (value instanceof Integer)
				setPos((Integer) value);
			return value instanceof Integer;
		case 5:
			if (value instanceof Index)
				setIndex((Index) value);
			return value instanceof Index;
		case 7:
			if (value instanceof Boolean)
				setKey((Boolean) value);
			return value instanceof Boolean;
		case 8:
			if (value instanceof Boolean)
				setMandatory((Boolean) value);
			return value instanceof Boolean;
		case 9:
			if (value instanceof String)
				setDefault((String) value);
			return value instanceof String;
		case 10:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		case 11:
			if (value instanceof Record)
				setRelated((Record) value);
			return value instanceof Record;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 6:
			return addValues();
		default:
			return null;
		}
	}
}