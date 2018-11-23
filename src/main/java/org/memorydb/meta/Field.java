package org.memorydb.meta;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.meta.Project.IndexRecords;
import org.memorydb.meta.Record.IndexFieldName;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;

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
		name = "mandatory",
		type = "BOOLEAN",
		mandatory = false
	)
	public boolean isMandatory() {
		return rec == 0 ? false : (store.getByte(rec, 14) & 1) > 0;
	}

	@FieldData(
		name = "minimum",
		type = "LONG",
		mandatory = false
	)
	public long getMinimum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 15);
	}

	@FieldData(
		name = "maximum",
		type = "LONG",
		mandatory = false
	)
	public long getMaximum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 23);
	}

	@FieldData(
		name = "format",
		type = "STRING",
		mandatory = false
	)
	public String getFormat() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 31));
	}

	@FieldData(
		name = "decimals",
		type = "BYTE",
		mandatory = false
	)
	public byte getDecimals() {
		return rec == 0 ? 0 : store.getByte(rec, 35);
	}

	@FieldData(
		name = "default",
		type = "STRING",
		mandatory = false
	)
	public String getDefault() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 36));
	}

	@FieldData(
		name = "condition",
		type = "STRING",
		mandatory = false
	)
	public String getCondition() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 40));
	}

	@FieldData(
		name = "description",
		type = "STRING",
		mandatory = false
	)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 44));
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
		if (getType() == Type.RELATION)
			return new Record(store, rec == 0 ? 0 : store.getInt(rec, 74));
		return null;
	}

	@FieldData(
		name = "record",
		type = "RELATION",
		related = Record.class,
		when = "INCLUDE",
		mandatory = false
	)
	public Record getRecord() {
		if (getType() == Type.INCLUDE)
			return new Record(store, rec == 0 ? 0 : store.getInt(rec, 78));
		return null;
	}

	@FieldData(
		name = "content",
		type = "RELATION",
		related = Record.class,
		when = "ARRAY",
		mandatory = false
	)
	public Record getContent() {
		if (getType() == Type.ARRAY)
			return new Record(store, rec == 0 ? 0 : store.getInt(rec, 82));
		return null;
	}

	@FieldData(
		name = "child",
		type = "RELATION",
		related = Record.class,
		when = "SUB",
		mandatory = false
	)
	public Record getChild() {
		if (getType() == Type.SUB)
			return new Record(store, rec == 0 ? 0 : store.getInt(rec, 86));
		return null;
	}

	@FieldData(
		name = "to",
		type = "RELATION",
		related = Record.class,
		when = "INDEX",
		mandatory = false
	)
	public Record getTo() {
		if (getType() == Type.INDEX)
			return new Record(store, rec == 0 ? 0 : store.getInt(rec, 90));
		return null;
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
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 57));
	}

	@Override
	public void output(Write write, int iterate) throws IOException {
		if (rec == 0 || iterate <= 0)
			return;
		write.field("name", getName());
		write.field("nr", getNr());
		write.field("type", getType());
		if (isKey())
			write.field("key", isKey());
		ValuesArray fldValues = getValues();
		if (fldValues != null && fldValues.getSize() > 0) {
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
		if (fldOrder != null && fldOrder.getSize() > 0) {
			write.sub("order");
			for (OrderArray sub : fldOrder)
				sub.output(write, iterate);
			write.endSub();
		}
		if (isMandatory())
			write.field("mandatory", isMandatory());
		if (getMinimum() != 0)
			write.field("minimum", getMinimum());
		if (getMaximum() != 0)
			write.field("maximum", getMaximum());
		write.field("format", getFormat());
		if (getDecimals() != 0)
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
		res.append("Record=").append("{").append(getUpRecord().keys()).append("}");
		res.append(", ");
		res.append("name").append("=").append(getName());
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

	public static int parseKey(Parser parser, int recordRecNr, Project project) {
		Field into = new Field(project.getStore(), 0);
		Record onRec = new Record(into.getStore());
		if (parser.hasField("Record")) {
			parser.getRelation("Record", (recNr1, idx1) -> {
				IndexRecords idx = project.new IndexRecords(new Record(project.getStore()), parser.getString("name"));
				int nr = idx.search();
				onRec.setRec(nr);
				return true;
			}, 0);
			if (onRec.getRec() == 0)
				return 0;
		} else {
			if (recordRecNr == 0)
				return 0;
			onRec.setRec(recordRecNr);
		}
		IndexFieldName idx = onRec.new IndexFieldName(into, parser.getString("name"));
		int recId = idx.search();
		if (recId == 0)
			return 0;
		return recId;
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
		case 5:
			return isMandatory();
		case 6:
			long min = getMinimum();
			return min == Long.MIN_VALUE ? null : min;
		case 7:
			long max = getMaximum();
			return max == Long.MIN_VALUE ? null : max;
		case 8:
			return getFormat();
		case 9:
			return getDecimals();
		case 10:
			return getDefault();
		case 11:
			return getCondition();
		case 12:
			return getDescription();
		case 13:
			return getValues();
		case 14:
			return getRelated();
		case 15:
			return getRecord();
		case 16:
			return getContent();
		case 17:
			return getChild();
		case 18:
			return getTo();
		case 19:
			return getOrder();
		default:
			return null;
		}
	}

	@Override
	public Iterable<? extends RecordInterface> iterate(int field, Object... key) {
		switch (field) {
		case 13:
			return getValues();
		case 19:
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
			return FieldType.BOOLEAN;
		case 6:
			return FieldType.LONG;
		case 7:
			return FieldType.LONG;
		case 8:
			return FieldType.STRING;
		case 9:
			return FieldType.INTEGER;
		case 10:
			return FieldType.STRING;
		case 11:
			return FieldType.STRING;
		case 12:
			return FieldType.STRING;
		case 13:
			return FieldType.ARRAY;
		case 14:
			return getType() == Type.RELATION ? FieldType.OBJECT : null;
		case 15:
			return getType() == Type.INCLUDE ? FieldType.OBJECT : null;
		case 16:
			return FieldType.OBJECT;
		case 17:
			return FieldType.OBJECT;
		case 18:
			return FieldType.OBJECT;
		case 19:
			return FieldType.ARRAY;
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
			return "mandatory";
		case 6:
			return "minimum";
		case 7:
			return "maximum";
		case 8:
			return "format";
		case 9:
			return "decimals";
		case 10:
			return "default";
		case 11:
			return "condition";
		case 12:
			return "description";
		case 13:
			return "values";
		case 14:
			return "related";
		case 15:
			return "record";
		case 16:
			return "content";
		case 17:
			return "child";
		case 18:
			return "to";
		case 19:
			return "order";
		default:
			return null;
		}
	}

	@Override
	public boolean exists() {
		return getRec() != 0;
	}
}
