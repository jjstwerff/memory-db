package org.memorydb.meta;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Index
 */
@RecordData(
	name = "Index",
	keyFields = {"name"})
public class Index implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 42;

	public Index(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Index(Store store, int rec) {
		rec = store.correct(rec);
		this.store = store;
		this.rec = rec;
	}

	@Override
	public int getRec() {
		return rec;
	}

	@Override
	public void setRec(int rec) {
		assert store.validate(rec);
		this.rec = rec;
	}

	@Override
	public Store getStore() {
		return store;
	}

	@Override
	public ChangeIndex change() {
		return new ChangeIndex(this);
	}

	@FieldData(
		name = "name",
		type = "STRING",
		mandatory = false
	)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(
		name = "indexFields",
		type = "ARRAY",
		related = IndexFieldsArray.class,
		mandatory = false
	)
	public IndexFieldsArray getIndexFields() {
		return new IndexFieldsArray(this, -1);
	}

	public IndexFieldsArray getIndexFields(int index) {
		return new IndexFieldsArray(this, index);
	}

	public IndexFieldsArray addIndexFields() {
		return getIndexFields().add();
	}

	@FieldData(
		name = "fieldPos",
		type = "INTEGER",
		mandatory = false
	)
	public int getFieldPos() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 12);
	}

	@FieldData(
		name = "flagPos",
		type = "INTEGER",
		mandatory = false
	)
	public int getFlagPos() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 16);
	}

	@FieldData(
		name = "parentPos",
		type = "INTEGER",
		mandatory = false
	)
	public int getParentPos() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 20);
	}

	@FieldData(
		name = "primary",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isPrimary() {
		return rec == 0 ? false : (store.getByte(rec, 24) & 1) > 0;
	}

	@FieldData(
		name = "record",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 25));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Project.class,
		mandatory = false
	)
	public Project getUpRecord() {
		return new Project(store, rec == 0 ? 0 : store.getInt(rec, 38));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		IndexFieldsArray fldIndexFields = getIndexFields();
		if (fldIndexFields != null) {
			write.sub("indexFields");
			for (IndexFieldsArray sub : fldIndexFields)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("fieldPos", getFieldPos());
		write.field("flagPos", getFlagPos());
		write.field("parentPos", getParentPos());
		write.field("primary", isPrimary());
		write.field("record", getRecord());
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Project").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("Name").append("=").append(getName());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		try {
			output(write, 4);
		} catch (IOException e) {
			return "";
		}
		return write.toString();
	}

	public Index parse(Parser parser, Project parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = parent.new IndexIndexes(this, name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeIndex record = new ChangeIndex(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeIndex record = new ChangeIndex(parent, 0)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeIndex record = new ChangeIndex(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	public boolean parseKey(Parser parser) {
		Project parent = getUpRecord();
		String name = parser.getString("name");
		int nextRec = parent.new IndexIndexes(this, name).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 3:
			return getFieldPos();
		case 4:
			return getFlagPos();
		case 5:
			return getParentPos();
		case 6:
			return isPrimary();
		case 7:
			return getRecord();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 2:
			return getIndexFields();
		default:
			return null;
		}
	}

	@Override
	public FieldType type(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.INTEGER;
		case 4:
			return FieldType.INTEGER;
		case 5:
			return FieldType.INTEGER;
		case 6:
			return FieldType.BOOLEAN;
		case 7:
			return FieldType.OBJECT;
		default:
			return null;
		}
	}

	@Override
	public String name(int field) {
		switch (field) {
		case 1:
			return "name";
		case 2:
			return "indexFields";
		case 3:
			return "fieldPos";
		case 4:
			return "flagPos";
		case 5:
			return "parentPos";
		case 6:
			return "primary";
		case 7:
			return "record";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
