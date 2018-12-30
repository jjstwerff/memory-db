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
	/* package private */ static final int RECORD_SIZE = 98;

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

	@FieldData(
		name = "nr",
		type = "INTEGER",
		mandatory = false
	)
	public int getNr() {
		return rec == 0 ? Integer.MIN_VALUE : store.getInt(rec, 8);
	}

	public enum Type {
		ARRAY, BOOLEAN, BYTE, DATE, ENUMERATE, FLOAT, INCLUDE, INDEX, INTEGER, LONG, NULL_BOOLEAN, RELATION, SUB, STRING;

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
		enumerate = {"ARRAY", "BOOLEAN", "BYTE", "DATE", "ENUMERATE", "FLOAT", "INCLUDE", "INDEX", "INTEGER", "LONG", "NULL_BOOLEAN", "RELATION", "SUB", "STRING"},
		condition = true,
		mandatory = false
	)
	public Field.Type getType() {
		int data = rec == 0 ? 0 : store.getByte(rec, 12) & 31;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "key",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isKey() {
		return rec == 0 ? false : (store.getByte(rec, 13) & 1) > 0;
	}

	@FieldData(
		name = "values",
		type = "ARRAY",
		related = ValuesArray.class,
		when = "ENUMERATE",
		mandatory = false
	)
	public ValuesArray getValues() {
		return new ValuesArray(this, -1);
	}

	public ValuesArray getValues(int index) {
		return getType() != Type.ENUMERATE ? new ValuesArray(store, 0, -1) : new ValuesArray(this, index);
	}

	public ValuesArray addValues() {
		return getType() != Type.ENUMERATE ? new ValuesArray(store, 0, -1) : getValues().add();
	}

	@FieldData(
		name = "related",
		type = "RELATION",
		related = Record.class,
		when = "RELATION",
		mandatory = false
	)
	public Record getRelated() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 40));
	}

	@FieldData(
		name = "record",
		type = "RELATION",
		related = Record.class,
		when = "INCLUDE",
		mandatory = false
	)
	public Record getRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 44));
	}

	@FieldData(
		name = "content",
		type = "RELATION",
		related = Record.class,
		when = "ARRAY",
		mandatory = false
	)
	public Record getContent() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 48));
	}

	@FieldData(
		name = "child",
		type = "RELATION",
		related = Record.class,
		when = "SUB",
		mandatory = false
	)
	public Record getChild() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 52));
	}

	@FieldData(
		name = "to",
		type = "RELATION",
		related = Record.class,
		when = "INDEX",
		mandatory = false
	)
	public Record getTo() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 56));
	}

	@FieldData(
		name = "order",
		type = "ARRAY",
		related = OrderArray.class,
		when = "INDEX",
		mandatory = false
	)
	public OrderArray getOrder() {
		return new OrderArray(this, -1);
	}

	public OrderArray getOrder(int index) {
		return getType() != Type.INDEX ? new OrderArray(store, 0, -1) : new OrderArray(this, index);
	}

	public OrderArray addOrder() {
		return getType() != Type.INDEX ? new OrderArray(store, 0, -1) : getOrder().add();
	}

	@Override
	@FieldData(
		name = "upRecord",
		type = "RELATION",
		related = Record.class,
		mandatory = false
	)
	public Record getUpRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 23));
	}

	@FieldData(
		name = "mandatory",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isMandatory() {
		return rec == 0 ? false : (store.getByte(rec, 64) & 1) > 0;
	}

	@FieldData(
		name = "minimum",
		type = "LONG",
		mandatory = false
	)
	public long getMinimum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 65);
	}

	@FieldData(
		name = "maximum",
		type = "LONG",
		mandatory = false
	)
	public long getMaximum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 73);
	}

	@FieldData(
		name = "format",
		type = "STRING",
		mandatory = false
	)
	public String getFormat() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 81));
	}

	@FieldData(
		name = "decimals",
		type = "BYTE",
		mandatory = false
	)
	public byte getDecimals() {
		return rec == 0 ? 0 : store.getByte(rec, 85);
	}

	@FieldData(
		name = "default",
		type = "STRING",
		mandatory = false
	)
	public String getDefault() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 86));
	}

	@FieldData(
		name = "condition",
		type = "STRING",
		mandatory = false
	)
	public String getCondition() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 90));
	}

	@FieldData(
		name = "description",
		type = "STRING",
		mandatory = false
	)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 94));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		write.field("nr", getNr());
		write.field("type", getType());
		write.field("key", isKey());
		ValuesArray fldValues = getValues();
		if (fldValues != null) {
			write.sub("values");
			for (ValuesArray sub : fldValues)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("related", getRelated());
		write.field("record", getRecord());
		write.field("content", getContent());
		write.field("child", getChild());
		write.field("to", getTo());
		OrderArray fldOrder = getOrder();
		if (fldOrder != null) {
			write.sub("order");
			for (OrderArray sub : fldOrder)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("mandatory", isMandatory());
		write.field("minimum", getMinimum());
		write.field("maximum", getMaximum());
		write.field("format", getFormat());
		write.field("decimals", getDecimals());
		write.field("default", getDefault());
		write.field("condition", getCondition());
		write.field("description", getDescription());
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
			int nextRec = parent.new IndexFieldName(this, name).search();
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
		parser.getRelation("Record", (recNr, idx) -> {
			parent.setRec(recNr);
			parent.parseKey(parser);
			return true;
		}, getRec());
		String name = parser.getString("name");
		int nextRec = parent.new IndexFieldName(this, name).search();
		parser.finishRelation();
		if (nextRec != 0)
			rec = nextRec;
		return nextRec != 0;
	}

	@Override
	public Object get(int field) {
		switch (field) {
		case 1:
			return getName();
		case 2:
			return getNr();
		case 3:
			return getType();
		case 4:
			return isKey();
		case 6:
			return getRelated();
		case 7:
			return getRecord();
		case 8:
			return getContent();
		case 9:
			return getChild();
		case 10:
			return getTo();
		case 13:
			return isMandatory();
		case 14:
			return getMinimum();
		case 15:
			return getMaximum();
		case 16:
			return getFormat();
		case 17:
			return getDecimals();
		case 18:
			return getDefault();
		case 19:
			return getCondition();
		case 20:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 5:
			return getValues();
		case 11:
			return getOrder();
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
			return FieldType.INTEGER;
		case 3:
			return FieldType.STRING;
		case 4:
			return FieldType.BOOLEAN;
		case 5:
			return FieldType.ARRAY;
		case 6:
			return FieldType.OBJECT;
		case 7:
			return FieldType.OBJECT;
		case 8:
			return FieldType.OBJECT;
		case 9:
			return FieldType.OBJECT;
		case 10:
			return FieldType.OBJECT;
		case 11:
			return FieldType.ARRAY;
		case 13:
			return FieldType.BOOLEAN;
		case 14:
			return FieldType.LONG;
		case 15:
			return FieldType.LONG;
		case 16:
			return FieldType.STRING;
		case 17:
			return FieldType.INTEGER;
		case 18:
			return FieldType.STRING;
		case 19:
			return FieldType.STRING;
		case 20:
			return FieldType.STRING;
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
			return "nr";
		case 3:
			return "type";
		case 4:
			return "key";
		case 5:
			return "values";
		case 6:
			return "related";
		case 7:
			return "record";
		case 8:
			return "content";
		case 9:
			return "child";
		case 10:
			return "to";
		case 11:
			return "order";
		case 13:
			return "mandatory";
		case 14:
			return "minimum";
		case 15:
			return "maximum";
		case 16:
			return "format";
		case 17:
			return "decimals";
		case 18:
			return "default";
		case 19:
			return "condition";
		case 20:
			return "description";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
