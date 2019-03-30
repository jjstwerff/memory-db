package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

/**
 * Automatically generated record class for table Field
 */
@RecordData(name = "Field")
public class Field implements Part {
	/* package private */ final Store store;
	protected final int rec;
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
	public int rec() {
		return rec;
	}

	@Override
	public Field copy(int newRec) {
		assert store.validate(newRec);
		return new Field(store, newRec);
	}

	@Override
	public Store store() {
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

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@Override
	@FieldData(name = "upRecord", type = "RELATION", related = Part.class, mandatory = false)
	public Part up() {
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
	public void output(Write write, int iterate) {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		outputPart(write, iterate);
		write.endRecord();
	}

	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Part").append("{").append(up().keys()).append("}");
		res.append(", ");
		res.append("Name").append("=").append(getName());
		return res.toString();
	}

	@Override
	public String toString() {
		Write write = new Write(new StringBuilder());
		output(write, 4);
		return write.toString();
	}

	public Field parse(Parser parser, Part parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = new Part.IndexObject(parent, name).search();
			if (parser.isDelete(nextRec)) {
				try (ChangeField record = new ChangeField(this)) {
					store.free(record.rec());
				}
				continue;
			}
			if (nextRec == 0) {
				try (ChangeField record = new ChangeField(parent, 0)) {
					record.setName(name);
					record.parseFields(parser);
				}
			} else {
				try (ChangeField record = new ChangeField(this)) {
					record.parseFields(parser);
				}
			}
		}
		return this;
	}

	@Override
	public boolean parseKey(Parser parser) {
		Part parent = up();
		String name = parser.getString("name");
		int nextRec = new Part.IndexObject(parent, name).search();
		parser.finishRelation();
		return nextRec != 0;
	}

	@Override
	public FieldType type() {
		return Part.super.getFieldType();
	}

	@Override
	public String name() {
		return Part.super.namePart(0);
	}

	@Override
	public RecordInterface copy() {
		return new Field(store, rec);
	}
}
