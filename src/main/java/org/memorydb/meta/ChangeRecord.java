package org.memorydb.meta;

import org.memorydb.file.Parser;
import org.memorydb.structure.ChangeInterface;
import java.util.Iterator;

/**
 * Automatically generated record class for table Record
 */
public class ChangeRecord extends Record implements ChangeInterface {
	/* package private */ ChangeRecord(Project parent, int rec) {
		super(parent.store(), rec == 0 ? parent.store().allocate(Record.RECORD_SIZE) : rec);
		if (rec == 0) {
			setName(null);
			store.setInt(rec(), 8, 0); // SET fieldName
			store.setInt(rec(), 12, 0); // SET fields
			setCondition(null);
			setDescription(null);
			up(parent);
		} else {
			up().new IndexRecords().remove(rec);
		}
	}

	/* package private */ ChangeRecord(Record current) {
		super(current.store, current.rec);
		if (rec != 0) {
			up().new IndexRecords().remove(rec);
		}
	}

	public void setName(String value) {
		store.setInt(rec, 4, store.putString(value));
	}

	public void setCondition(Field value) {
		store.setInt(rec, 16, value == null ? 0 : value.rec());
	}

	public void setDescription(String value) {
		store.setInt(rec, 20, store.putString(value));
	}

	private void up(Project value) {
		store.setInt(rec, 33, value == null ? 0 : value.rec());
		store.setInt(rec, 38, value == null ? 0 : value.index());
	}

	/* package private */ void parseFields(Parser parser) {
		if (parser.hasSub("fieldName")) {
			new Field(store).parse(parser, this);
		}
		if (parser.hasSub("fields")) {
			new Field(store).parse(parser, this);
		}
		if (parser.hasField("condition")) {
			parser.getRelation("condition", (recNr, idx) -> {
				Iterator<Field> iterator = getFields().iterator();
				Field relRec = iterator != null && iterator.hasNext() ? iterator.next() : null;
				if (relRec != null)
					relRec = relRec.parseKey(parser);
				ChangeRecord old = copy(recNr);
				old.setCondition(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasField("description")) {
			setDescription(parser.getString("description"));
		}
	}

	@Override
	public void close() {
		up().new IndexRecords().insert(rec());
	}

	@Override
	public boolean java(Object value) {
		int field = 0;
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
	public ChangeInterface add() {
		int field = 0;
		switch (field) {
		case 2:
			return addFieldName();
		case 3:
			return addFields();
		default:
			return null;
		}
	}

	@Override
	public ChangeRecord copy(int newRec) {
		assert store.validate(newRec);
		return new ChangeRecord(up(), newRec);
	}
}
