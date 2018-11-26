package org.memorydb.json;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Field
 */
@RecordData(
	name = "Field",
	keyFields = {"name"})
public class Field implements Part {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 35;

	public Field(Store store) {
		this.store = store;
		this.rec = 0;
	}

	public Field(Store store, int rec) {
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
	public int partPosition() {
		return 26;
	}

	@Override
	public ChangeField change() {
		return new ChangeField(this);
	}

	@FieldData(
		name = "name",
		type = "STRING",
		mandatory = false
	)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Part.class,
		mandatory = false
	)
	public Part getUpRecord() {
		if (rec == 0)
			return null;
		switch (store.getByte(rec, 21)) {
		case 1:
			return new Field(store, store.getInt(rec, 17));
		case 2:
			return new ArrayArray(store, store.getInt(rec, 17), store.getInt(rec, 22));
		case 3:
			return new Json(store, store.getInt(rec, 17));
		default:
			return null;
		}
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		outputPart(write, iterate);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Part").append("{").append(getUpRecord().keys()).append("}");
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

	public Field parse(Parser parser, Part parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new Part.IndexObject(parent, this, name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeField record = new ChangeField(this)) {
					store.free(record.getRec());
					record.setRec(0);
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeField record = new ChangeField(parent, 0)) {
					record.setName(name);
					record.parseFields(parser);
					rec = record.rec;
				}
			} else {
				rec = nextRec;
				try (ChangeField record = new ChangeField(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	@Override
	public boolean parseKey(Parser parser) {
		Part parent = getUpRecord();
		String name = parser.getString("name");
		int nextRec = new Part.IndexObject(parent, this, name).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		return Part.super.getPart(field);
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		if (field >= 2 && field <= 9)
			return Part.super.iteratePart(field - 2);
		switch (field) {
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		return Part.super.getFieldType();
	}

	@Override
	public FieldType type(int field) {
		return Part.super.typePart(field);
	}

	@Override
	public String name(int field) {
		return Part.super.namePart(field);
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
