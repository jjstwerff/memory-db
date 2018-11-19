package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import java.util.Iterator;

/**
 * Automatically generated record class for table Record
 */
public class ChangeRecord extends Record implements ChangeInterface {
	/* package private */ ChangeRecord(Project parent, int rec) {
		super(parent.getStore(), rec);
		if (rec == 0) {
			setRec(getStore().allocate(Record.RECORD_SIZE));
			System.out.println("record: " + getRec());
		}
		setName(null);
		store.setInt(getRec(), 8, 0); // SET fieldName
		store.setInt(getRec(), 12, 0); // SET fields
		setCondition(null);
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

	public void setCondition(Field value) {
		store.setInt(rec, 16, value == null ? 0 : value.getRec());
	}

	public void setDescription(String value) {
		store.setInt(rec, 20, store.putString(value));
	}

	public void setUpRecord(Project value) {
		store.setInt(rec, 33, value == null ? 0 : value.getRec());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("fieldName")) {
			new Field(store).parse(parser, this);
		}
		if (parser.hasSub("fields")) {
			new Field(store).parse(parser, this);
		}
		if (parser.hasField("condition")) {
			parser.getRelation("condition", (int recNr) -> {
				Field relRec = new Field(store, 0);
				boolean found = relRec.parseKey(parser);
				setRec(recNr);
				setCondition(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
	}

	@Override
	public void close() {
		System.out.println("close record: " + getRec());
		getUpRecord().new IndexRecords(this).insert(getRec());
	}

	@Override
	public boolean set(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof String)
				setName((String) value);
			return value instanceof String;
		case 4:
			if (value instanceof Field)
				setCondition((Field) value);
			return value instanceof Field;
		case 5:
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
			return addFieldName();
		case 3:
			return addFields();
		default:
			return null;
		}
	}
}