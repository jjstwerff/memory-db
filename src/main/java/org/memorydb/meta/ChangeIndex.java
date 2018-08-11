package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import java.util.Iterator;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Index
 */
public class ChangeIndex extends Index implements ChangeInterface {
	/* package private */ ChangeIndex(Project parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Index.RECORD_SIZE));
		}
		setName(null);
		store.setInt(getRec(), 8, 0); // ARRAY indexFields
		setFieldPos(0);
		setFlagPos(0);
		setParentPos(0);
		setPrimary(false);
		setRecord(null);
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexIndexes(this).remove(rec);
		}
	}

	/* package private */ ChangeIndex(Index current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexIndexes(this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void moveIndexFields(ChangeIndex other) {
		getStore().setInt(getRec(), 8, getStore().getInt(other.getRec(), 8));
		getStore().setInt(other.getRec(), 8, 0);
	}

	public void setFieldPos(int value) {
		store.setInt(rec, 12, value);
	}

	public void setFlagPos(int value) {
		store.setInt(rec, 16, value);
	}

	public void setParentPos(int value) {
		store.setInt(rec, 20, value);
	}

	public void setPrimary(boolean value) {
		store.setByte(rec, 24, (store.getByte(rec, 24) & 254) + (value ? 1 : 0));
	}

	public void setRecord(Record value) {
		store.setInt(rec, 25, value == null ? 0 : value.getRec());
	}

	public void setUpRecord(Project value) {
		store.setInt(rec, 38, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("indexFields")) {
			try (IndexFieldsArray sub = new IndexFieldsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("fieldPos")) {
			setFieldPos(parser.getInt("fieldPos"));
		}
		if (parser.hasField("flagPos")) {
			setFlagPos(parser.getInt("flagPos"));
		}
		if (parser.hasField("parentPos")) {
			setParentPos(parser.getInt("parentPos"));
		}
		if (parser.hasField("primary")) {
			Boolean valuePrimary = parser.getBoolean("primary");
			if (valuePrimary == null)
				throw new MutationException("Mandatory 'primary' field");
			setPrimary(valuePrimary);
		}
		if (parser.hasField("record")) {
			parser.getRelation("record", (int recNr) -> {
				Iterator<Record> iterator = getUpRecord().getRecords().iterator();
				Record relRec = iterator.hasNext() ? iterator.next() : null;
				boolean found = relRec != null && relRec.parseKey(parser);
				setRec(recNr);
				setRecord(relRec);
				return found;
			}, getRec());
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexIndexes(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 3:
			if (value instanceof Integer)
				setFieldPos((Integer) value);
			return value instanceof Integer;
		case 4:
			if (value instanceof Integer)
				setFlagPos((Integer) value);
			return value instanceof Integer;
		case 5:
			if (value instanceof Integer)
				setParentPos((Integer) value);
			return value instanceof Integer;
		case 6:
			if (value instanceof Boolean)
				setPrimary((Boolean) value);
			return value instanceof Boolean;
		case 7:
			if (value instanceof Record)
				setRecord((Record) value);
			return value instanceof Record;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 2:
			return addIndexFields();
		default:
			return null;
		}
	}
}