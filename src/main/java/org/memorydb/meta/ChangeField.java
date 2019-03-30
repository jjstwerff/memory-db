package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import org.memorydb.structure.MemoryRecord;

import java.util.Iterator;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Field
 */
public class ChangeField extends Field implements MemoryRecord, ChangeInterface {
	/* package private */ ChangeField(Record parent, int rec) {
		super(parent.store(), rec);
		setName(null);
		setNr(0);
		setType(Field.Type.STRING);
		setKey(false);
		store.setInt(rec(), 13, 0); // ARRAY values
		setRelated(null);
		setRecord(null);
		setContent(null);
		setChild(null);
		setTo(null);
		store.setInt(rec(), 17, 0); // ARRAY order
		setMandatory(false);
		setMinimum(0L);
		setMaximum(0L);
		setFormat(null);
		setDecimals((byte) 0);
		setDefault(null);
		setCondition(null);
		setDescription(null);
		up(parent);
		if (rec != 0) {
			up().new IndexFieldName().remove(rec);
			up().new IndexFields().remove(rec);
		}
	}

	/* package private */ ChangeField(Field current) {
		super(current.store, current.rec);
		if (rec != 0) {
			up().new IndexFieldName().remove(rec);
			up().new IndexFields().remove(rec);
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

	public void moveValues(ChangeField other) {
		store().setInt(rec(), 13, store().getInt(other.rec(), 13));
		store().setInt(other.rec(), 13, 0);
	}

	public void setRelated(Record value) {
		if (getType() == Type.RELATION) {
			store.setInt(rec, 13, value == null ? 0 : value.rec());
		}
	}

	public void setRecord(Record value) {
		if (getType() == Type.INCLUDE) {
			store.setInt(rec, 13, value == null ? 0 : value.rec());
		}
	}

	public void setContent(Record value) {
		if (getType() == Type.ARRAY) {
			store.setInt(rec, 13, value == null ? 0 : value.rec());
		}
	}

	public void setChild(Record value) {
		if (getType() == Type.SUB) {
			store.setInt(rec, 13, value == null ? 0 : value.rec());
		}
	}

	public void setTo(Record value) {
		if (getType() == Type.INDEX) {
			store.setInt(rec, 13, value == null ? 0 : value.rec());
		}
	}

	public void moveOrder(ChangeField other) {
		store().setInt(rec(), 17, store().getInt(other.rec(), 17));
		store().setInt(other.rec(), 17, 0);
	}

	public void setMandatory(boolean value) {
		store.setByte(rec, 21, (store.getByte(rec, 21) & 254) + (value ? 1 : 0));
	}

	public void setMinimum(long value) {
		store.setLong(rec, 22, value);
	}

	public void setMaximum(long value) {
		store.setLong(rec, 30, value);
	}

	public void setFormat(String value) {
		store.setInt(rec, 38, store.putString(value));
	}

	public void setDecimals(byte value) {
		store.setByte(rec, 42, value);
	}

	public void setDefault(String value) {
		store.setInt(rec, 43, store.putString(value));
	}

	public void setCondition(String value) {
		store.setInt(rec, 47, store.putString(value));
	}

	public void setDescription(String value) {
		store.setInt(rec, 51, store.putString(value));
	}

	private void up(Record value) {
		store.setInt(rec, 23, value == null ? 0 : value.rec());
		store.setInt(rec, 28, value == null ? 0 : value.index());
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
		if (parser.hasSub("values")) {
			try (ValuesArray sub = new ValuesArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("related")) {
			parser.getRelation("related", (recNr, idx) -> {
				Iterator<Record> iterator = up().up().getRecords().iterator();
				Record relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeField old = copy(recNr);
				old.setRelated(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("record")) {
			parser.getRelation("record", (recNr, idx) -> {
				Iterator<Record> iterator = up().up().getRecords().iterator();
				Record relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeField old = copy(recNr);
				old.setRecord(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("content")) {
			parser.getRelation("content", (recNr, idx) -> {
				Iterator<Record> iterator = up().up().getRecords().iterator();
				Record relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeField old = copy(recNr);
				old.setContent(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("child")) {
			parser.getRelation("child", (recNr, idx) -> {
				Iterator<Record> iterator = up().up().getRecords().iterator();
				Record relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeField old = copy(recNr);
				old.setChild(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("to")) {
			parser.getRelation("to", (recNr, idx) -> {
				Iterator<Record> iterator = up().up().getRecords().iterator();
				Record relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeField old = copy(recNr);
				old.setTo(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasSub("order")) {
			try (OrderArray sub = new OrderArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
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
	}

	@Override
	public void close() {
		up().new IndexFieldName().insert(rec());
		up().new IndexFields().insert(rec());
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
		case 6:
			if (value instanceof Record)
				setRelated((Record) value);
			return value instanceof Record;
		case 7:
			if (value instanceof Record)
				setRecord((Record) value);
			return value instanceof Record;
		case 8:
			if (value instanceof Record)
				setContent((Record) value);
			return value instanceof Record;
		case 9:
			if (value instanceof Record)
				setChild((Record) value);
			return value instanceof Record;
		case 10:
			if (value instanceof Record)
				setTo((Record) value);
			return value instanceof Record;
		case 12:
			if (value instanceof Boolean)
				setMandatory((Boolean) value);
			return value instanceof Boolean;
		case 13:
			if (value instanceof Long)
				setMinimum((Long) value);
			return value instanceof Long;
		case 14:
			if (value instanceof Long)
				setMaximum((Long) value);
			return value instanceof Long;
		case 15:
			if (value instanceof String)
				setFormat((String) value);
			return value instanceof String;
		case 16:
			if (value instanceof Byte)
				setDecimals((Byte) value);
			return value instanceof Byte;
		case 17:
			if (value instanceof String)
				setDefault((String) value);
			return value instanceof String;
		case 18:
			if (value instanceof String)
				setCondition((String) value);
			return value instanceof String;
		case 19:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 5:
			return addValues();
		case 11:
			return addOrder();
		default:
			return null;
		}
	}

	@Override
	public ChangeField copy(int rec) {
		assert store.validate(rec);
		return new ChangeField(up(), rec);
	}
}