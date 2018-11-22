package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
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
		setNr(0);
		setType(Field.Type.STRING);
		setKey(false);
		setMandatory(false);
		setMinimum(0L);
		setMaximum(0L);
		setFormat(null);
		setDecimals((byte) 0);
		setDefault(null);
		setCondition(null);
		setDescription(null);
		store.setInt(getRec(), 70, 0); // ARRAY values
		setRelated(null);
		setRecord(null);
		setContent(null);
		setChild(null);
		setTo(null);
		store.setInt(getRec(), 94, 0); // ARRAY order
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexFieldName(this).remove(rec);
			getUpRecord().new IndexFields(this).remove(rec);
		}
	}

	/* package private */ ChangeField(Field current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexFieldName(this).remove(rec);
			getUpRecord().new IndexFields(this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setNr(int value) {
		store.setInt(rec, 8, value);
	}

	public void setType(Field.Type value) {
		if (value == null)
			throw new MutationException("Mandatory 'type' field");
		store.setByte(rec, 12, (store.getByte(rec, 12) & 224) + 1 + value.ordinal());
	}

	public void setKey(boolean value) {
		store.setByte(rec, 13, (store.getByte(rec, 13) & 254) + (value ? 1 : 0));
	}

	public void setMandatory(boolean value) {
		store.setByte(rec, 14, (store.getByte(rec, 14) & 254) + (value ? 1 : 0));
	}

	public void setMinimum(long value) {
		store.setLong(rec, 15, value);
	}

	public void setMaximum(long value) {
		store.setLong(rec, 23, value);
	}

	public void setFormat(String value) {
		store.setInt(rec, 31, store.putString(value));
	}

	public void setDecimals(byte value) {
		store.setByte(rec, 35, value);
	}

	public void setDefault(String value) {
		store.setInt(rec, 36, store.putString(value));
	}

	public void setCondition(String value) {
		store.setInt(rec, 40, store.putString(value));
	}

	public void setDescription(String value) {
		store.setInt(rec, 44, store.putString(value));
	}

	public void moveValues(ChangeField other) {
		getStore().setInt(getRec(), 70, getStore().getInt(other.getRec(), 70));
		getStore().setInt(other.getRec(), 70, 0);
	}

	public void setRelated(Record value) {
		if (getType() == Type.RELATION) {
			store.setInt(rec, 74, value == null ? 0 : value.getRec());
		}
	}

	public void setRecord(Record value) {
		if (getType() == Type.INCLUDE) {
			store.setInt(rec, 78, value == null ? 0 : value.getRec());
		}
	}

	public void setContent(Record value) {
		if (getType() == Type.ARRAY) {
			store.setInt(rec, 82, value == null ? 0 : value.getRec());
		}
	}

	public void setChild(Record value) {
		if (getType() == Type.SUB) {
			store.setInt(rec, 86, value == null ? 0 : value.getRec());
		}
	}

	public void setTo(Record value) {
		if (getType() == Type.INDEX) {
			store.setInt(rec, 90, value == null ? 0 : value.getRec());
		}
	}

	public void moveOrder(ChangeField other) {
		getStore().setInt(getRec(), 94, getStore().getInt(other.getRec(), 94));
		getStore().setInt(other.getRec(), 94, 0);
	}

	public void setUpRecord(Record value) {
		store.setInt(rec, 57, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasField("nr")) {
			setNr(parser.getInt("nr"));
		}
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field Field.type");
			setType(valueType == null ? null : type);
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
		if (parser.hasField("minimum")) {
			setMinimum(parser.getLong("minimum"));
		}
		if (parser.hasField("maximum")) {
			setMaximum(parser.getLong("maximum"));
		}
		if (parser.hasField("format")) {
			setFormat(parser.getString("format"));
		}
		if (parser.hasField("decimals")) {
			setDecimals((byte) parser.getInt("decimals"));
		}
		if (parser.hasField("default")) {
			setDefault(parser.getString("default"));
		}
		if (parser.hasField("condition")) {
			setCondition(parser.getString("condition"));
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
		if (parser.hasSub("values")) {
			try (ValuesArray sub = new ValuesArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("related")) {
			parser.getRelation("related", (int recNr) -> {
				int nr = Record.parseKey(parser, getUpRecord().getUpRecord());
				if (nr == 0)
					return false;
				setRec(recNr);
				setRelated(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasField("record")) {
			parser.getRelation("record", (int recNr) -> {
				int nr = Record.parseKey(parser, getUpRecord().getUpRecord());
				if (nr == 0)
					return false;
				setRec(recNr);
				setRecord(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasField("content")) {
			parser.getRelation("content", (int recNr) -> {
				int nr = Record.parseKey(parser, getUpRecord().getUpRecord());
				if (nr == 0)
					return false;
				setRec(recNr);
				setContent(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasField("child")) {
			parser.getRelation("child", (int recNr) -> {
				int nr = Record.parseKey(parser, getUpRecord().getUpRecord());
				if (nr == 0)
					return false;
				setRec(recNr);
				setChild(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasField("to")) {
			parser.getRelation("to", (int recNr) -> {
				int nr = Record.parseKey(parser, getUpRecord().getUpRecord());
				if (nr == 0)
					return false;
				setRec(recNr);
				setTo(new Record(store, nr));
				return true;
			}, getRec());
		}
		if (parser.hasSub("order")) {
			try (OrderArray sub = new OrderArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexFieldName(this).insert(getRec());
		getUpRecord().new IndexFields(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
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
			if (value instanceof Field.Type)
				setType((Field.Type) value);
			return value instanceof Field.Type;
		case 4:
			if (value instanceof Boolean)
				setKey((Boolean) value);
			return value instanceof Boolean;
		case 5:
			if (value instanceof Boolean)
				setMandatory((Boolean) value);
			return value instanceof Boolean;
		case 6:
			if (value instanceof Long)
				setMinimum((Long) value);
			return value instanceof Long;
		case 7:
			if (value instanceof Long)
				setMaximum((Long) value);
			return value instanceof Long;
		case 8:
			if (value instanceof String)
				setFormat((String) value);
			return value instanceof String;
		case 9:
			if (value instanceof Byte)
				setDecimals((Byte) value);
			return value instanceof Byte;
		case 10:
			if (value instanceof String)
				setDefault((String) value);
			return value instanceof String;
		case 11:
			if (value instanceof String)
				setCondition((String) value);
			return value instanceof String;
		case 12:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		case 14:
			if (value instanceof Record)
				setRelated((Record) value);
			return value instanceof Record;
		case 15:
			if (value instanceof Record)
				setRecord((Record) value);
			return value instanceof Record;
		case 16:
			if (value instanceof Record)
				setContent((Record) value);
			return value instanceof Record;
		case 17:
			if (value instanceof Record)
				setChild((Record) value);
			return value instanceof Record;
		case 18:
			if (value instanceof Record)
				setTo((Record) value);
			return value instanceof Record;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 13:
			return addValues();
		case 19:
			return addOrder();
		default:
			return null;
		}
	}
}