package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import java.util.Iterator;
import org.memorydb.handler.MutationException;

/**
 * Automatically generated record class for table Record
 */
public class ChangeRecord extends Record implements ChangeInterface {
	/* package private */ ChangeRecord(Project parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Record.RECORD_SIZE));
		}
		setName(null);
		store.setInt(getRec(), 8, 0); // ARRAY fields
		store.setInt(getRec(), 12, 0); // SET fieldOnName
		store.setInt(getRec(), 16, 0); // SET setIndexes
		store.setInt(getRec(), 20, 0); // SET freeBits
		setParent(null);
		setSize(0);
		setRelated(false);
		setFull(false);
		setDescription(null);
		setUpRecord(parent);
		if (rec != 0) {
			getUpRecord().new IndexRecords(this).remove(rec);
		}
	}

	/* package private */ ChangeRecord(Record current) {
		super(current.store, current.rec);
		if (rec != 0) {
			getUpRecord().new IndexRecords(this).remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void moveFields(ChangeRecord other) {
		getStore().setInt(getRec(), 8, getStore().getInt(other.getRec(), 8));
		getStore().setInt(other.getRec(), 8, 0);
	}

	public void setParent(Record value) {
		store.setInt(rec, 24, value == null ? 0 : value.getRec());
	}

	public void setSize(int value) {
		if (value == Integer.MIN_VALUE)
			throw new MutationException("Mandatory 'size' field");
		store.setInt(rec, 28, value);
	}

	public void setRelated(boolean value) {
		store.setByte(rec, 32, (store.getByte(rec, 32) & 254) + (value ? 1 : 0));
	}

	public void setFull(boolean value) {
		store.setByte(rec, 33, (store.getByte(rec, 33) & 254) + (value ? 1 : 0));
	}

	public void setDescription(String value) {
		store.setInt(rec, 34, store.putString(value));
	}

	public void setUpRecord(Project value) {
		store.setInt(rec, 47, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("fields")) {
			try (FieldsArray sub = new FieldsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("fieldOnName")) {
			new Field(store).parse(parser, this);
		}
		if (parser.hasSub("setIndexes")) {
			new SetIndex(store).parse(parser, this);
		}
		if (parser.hasSub("freeBits")) {
			new FreeBits(store).parse(parser, this);
		}
		if (parser.hasField("parent")) {
			parser.getRelation("parent", (int recNr) -> {
				Iterator<Record> iterator = getUpRecord().getRecords().iterator();
				Record relRec = iterator.hasNext() ? iterator.next() : null;
				boolean found = relRec != null && relRec.parseKey(parser);
				setRec(recNr);
				setParent(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasField("size")) {
			setSize(parser.getInt("size"));
		}
		if (parser.hasField("related")) {
			Boolean valueRelated = parser.getBoolean("related");
			if (valueRelated == null)
				throw new MutationException("Mandatory 'related' field");
			setRelated(valueRelated);
		}
		if (parser.hasField("full")) {
			Boolean valueFull = parser.getBoolean("full");
			if (valueFull == null)
				throw new MutationException("Mandatory 'full' field");
			setFull(valueFull);
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
	}

	@Override
	public void close() {
		getUpRecord().new IndexRecords(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 6:
			if (value instanceof Record)
				setParent((Record) value);
			return value instanceof Record;
		case 7:
			if (value instanceof Integer)
				setSize((Integer) value);
			return value instanceof Integer;
		case 8:
			if (value instanceof Boolean)
				setRelated((Boolean) value);
			return value instanceof Boolean;
		case 9:
			if (value instanceof Boolean)
				setFull((Boolean) value);
			return value instanceof Boolean;
		case 10:
			if (value instanceof String)
				setDescription((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	@Override
	public ChangeInterface add(int field) {
		switch (field) {
		case 2:
			return addFields();
		case 3:
			return addFieldOnName();
		case 4:
			return addSetIndexes();
		case 5:
			return addFreeBits();
		default:
			return null;
		}
	}
}