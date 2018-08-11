package org.memorydb.meta;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table Field
 */
@RecordData(
	name = "Field",
	keyFields = {"name"})
public class Field implements MemoryRecord, RecordInterface {
	/* package private */ Store store;
	protected int rec;
	/* package private */ static final int RECORD_SIZE = 49;

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

	public enum Type {
		ARRAY, BOOLEAN, BYTE, DATE, ENUMERATE, FLOAT, INTEGER, LONG, NULL_BOOLEAN, RELATION, SET, SUB, STRING;

		private static Map<String, Type> map = new HashMap<>();

		static {
			for (Type tp : Type.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Type get(String val) {
			return map.containsKey(val) ? map.get(val) : null;
		}
	}

	@FieldData(
		name = "type",
		type = "ENUMERATE",
		enumerate = {"ARRAY", "BOOLEAN", "BYTE", "DATE", "ENUMERATE", "FLOAT", "INTEGER", "LONG", "NULL_BOOLEAN", "RELATION", "SET", "SUB", "STRING"},
		mandatory = false
	)
	public Field.Type getType() {
		int data = rec == 0 ? 0 : store.getByte(rec, 8) & 31;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "auto",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isAuto() {
		return rec == 0 ? false : (store.getByte(rec, 9) & 1) > 0;
	}

	@FieldData(
		name = "pos",
		type = "INTEGER",
		mandatory = true
	)
	public int getPos() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 10);
	}

	@FieldData(
		name = "index",
		type = "RELATION",
		related = Index.class,
		mandatory = false
	)
	public Index getIndex() {
		return new Index(store, rec == 0 ? 0 : store.getInt(rec, 14));
	}

	@FieldData(
		name = "values",
		type = "ARRAY",
		related = ValuesArray.class,
		mandatory = false
	)
	public ValuesArray getValues() {
		return new ValuesArray(this, -1);
	}

	public ValuesArray getValues(int index) {
		return new ValuesArray(this, index);
	}

	public ValuesArray addValues() {
		return getValues().add();
	}

	@FieldData(
		name = "key",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isKey() {
		return rec == 0 ? false : (store.getByte(rec, 22) & 1) > 0;
	}

	@FieldData(
		name = "mandatory",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isMandatory() {
		return rec == 0 ? false : (store.getByte(rec, 23) & 1) > 0;
	}

	@FieldData(
		name = "default",
		type = "STRING",
		mandatory = false
	)
	public String getDefault() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 24));
	}

	@FieldData(
		name = "description",
		type = "STRING",
		mandatory = false
	)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 28));
	}

	@FieldData(
		name = "related",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getRelated() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 45));
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getUpRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 41));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName(), true);
		write.field("type", getType(), false);
		write.field("auto", isAuto(), false);
		write.field("pos", getPos(), false);
		write.field("index", getIndex(), false);
		ValuesArray fldValues = getValues();
		if (fldValues != null) {
			write.sub("values", false);
			for (ValuesArray sub : fldValues)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		write.field("key", isKey(), false);
		write.field("mandatory", isMandatory(), false);
		write.field("default", getDefault(), false);
		write.field("description", getDescription(), false);
		write.field("related", getRelated(), false);
		write.endRecord();
	}

	@Override
	public String keys() throws IOException {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Record").append("{").append(getUpRecord().keys()).append("}");
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

	public Field parse(Parser parser, Record parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = parent.new IndexFieldOnName(this, name).search();
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

	public boolean parseKey(Parser parser) {
		Record parent = getUpRecord();
		parser.getRelation("Record", (int recNr) -> {
			parent.setRec(recNr);
			parent.parseKey(parser);
			return true;
		}, getRec());
		String name = parser.getString("name");
		int nextRec = parent.new IndexFieldOnName(this, name).search();
		parser.finishRelation();
		rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getType();
		case 3:
			return isAuto();
		case 4:
			return getPos();
		case 5:
			return getIndex();
		case 7:
			return isKey();
		case 8:
			return isMandatory();
		case 9:
			return getDefault();
		case 10:
			return getDescription();
		case 11:
			return getRelated();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 6:
			return getValues();
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
			return FieldType.STRING;
		case 3:
			return FieldType.BOOLEAN;
		case 4:
			return FieldType.INTEGER;
		case 5:
			return FieldType.OBJECT;
		case 6:
			return FieldType.ITERATE;
		case 7:
			return FieldType.BOOLEAN;
		case 8:
			return FieldType.BOOLEAN;
		case 9:
			return FieldType.STRING;
		case 10:
			return FieldType.STRING;
		case 11:
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
			return "type";
		case 3:
			return "auto";
		case 4:
			return "pos";
		case 5:
			return "index";
		case 6:
			return "values";
		case 7:
			return "key";
		case 8:
			return "mandatory";
		case 9:
			return "default";
		case 10:
			return "description";
		case 11:
			return "related";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
