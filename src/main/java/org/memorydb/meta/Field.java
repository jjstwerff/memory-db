package org.memorydb.meta;

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
@RecordData(name = "Field")
public class Field implements MemoryRecord, RecordInterface {
	/* package private */ final Store store;
	protected final int rec;
	/* package private */ static final int RECORD_SIZE = 55;

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
	public Store store() {
		return store;
	}

	@Override
	public ChangeField change() {
		return new ChangeField(this);
	}

	@FieldData(name = "name", type = "STRING", mandatory = false)
	public String getName() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 4));
	}

	@FieldData(name = "nr", type = "INTEGER", mandatory = false)
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

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "ARRAY", "BOOLEAN", "BYTE", "DATE", "ENUMERATE", "FLOAT", "INCLUDE", "INDEX", "INTEGER", "LONG", "NULL_BOOLEAN", "RELATION", "SUB", "STRING" }, condition = true, mandatory = false)
	public Field.Type getType() {
		int data = rec == 0 ? 0 : store.getByte(rec, 12) & 31;
		if (data <= 0 || data > Type.values().length)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "key", type = "BOOLEAN", mandatory = false)
	public boolean isKey() {
		return rec == 0 ? false : (store.getByte(rec, 13) & 1) > 0;
	}

	@FieldData(name = "values", type = "ARRAY", related = ValuesArray.class, when = "ENUMERATE", mandatory = false)
	public ValuesArray getValues() {
		return new ValuesArray(this, -1);
	}

	public ValuesArray getValues(int index) {
		return getType() != Type.ENUMERATE ? new ValuesArray(store, 0, -1) : new ValuesArray(this, index);
	}

	public ValuesArray addValues() {
		return getType() != Type.ENUMERATE ? new ValuesArray(store, 0, -1) : getValues().add();
	}

	@FieldData(name = "related", type = "RELATION", related = Record.class, when = "RELATION", mandatory = false)
	public Record getRelated() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "record", type = "RELATION", related = Record.class, when = "INCLUDE", mandatory = false)
	public Record getRecord() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "content", type = "RELATION", related = Record.class, when = "ARRAY", mandatory = false)
	public Record getContent() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "child", type = "RELATION", related = Record.class, when = "SUB", mandatory = false)
	public Record getChild() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "to", type = "RELATION", related = Record.class, when = "INDEX", mandatory = false)
	public Record getTo() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 13));
	}

	@FieldData(name = "order", type = "ARRAY", related = OrderArray.class, when = "INDEX", mandatory = false)
	public OrderArray getOrder() {
		return new OrderArray(this, -1);
	}

	public OrderArray getOrder(int index) {
		return getType() != Type.INDEX ? new OrderArray(store, 0, -1) : new OrderArray(this, index);
	}

	public OrderArray addOrder() {
		return getType() != Type.INDEX ? new OrderArray(store, 0, -1) : getOrder().add();
	}

	@FieldData(name = "mandatory", type = "BOOLEAN", mandatory = false)
	public boolean isMandatory() {
		return rec == 0 ? false : (store.getByte(rec, 21) & 1) > 0;
	}

	@FieldData(name = "minimum", type = "LONG", mandatory = false)
	public long getMinimum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 22);
	}

	@FieldData(name = "maximum", type = "LONG", mandatory = false)
	public long getMaximum() {
		return rec == 0 ? Long.MIN_VALUE : store.getLong(rec, 30);
	}

	@FieldData(name = "format", type = "STRING", mandatory = false)
	public String getFormat() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 38));
	}

	@FieldData(name = "decimals", type = "BYTE", mandatory = false)
	public byte getDecimals() {
		return rec == 0 ? 0 : store.getByte(rec, 42);
	}

	@FieldData(name = "default", type = "STRING", mandatory = false)
	public String getDefault() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 43));
	}

	@FieldData(name = "condition", type = "STRING", mandatory = false)
	public String getCondition() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 47));
	}

	@FieldData(name = "description", type = "STRING", mandatory = false)
	public String getDescription() {
		return rec == 0 ? null : store.getString(store.getInt(rec, 51));
	}

	@Override
	public Record up() {
		return new Record(store, rec == 0 ? 0 : store.getInt(rec, 23));
	}

	@Override
	public void output(Write write, int iterate) {
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
	public String keys() {
		StringBuilder res = new StringBuilder();
		if (rec == 0)
			return "";
		res.append("Record").append("{").append(up().keys()).append("}");
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

	public Field parse(Parser parser, Record parent) {
		while (parser.getSub()) {
			String name = parser.getString("name");
			int nextRec = parent.new IndexFieldName(name).search();
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

	public Field parseKey(Parser parser) {
		int rec[] = new int[1];
		parser.getRelation("Record", (recNr, idx) -> {
			rec[0] = up().parseKey(parser).rec();
			return true;
		}, rec());
		Record parent = rec[0] > 0 ? up().copy(rec[0]) : up();
		String name = parser.getString("name");
		int nextRec = parent.new IndexFieldName(name).search();
		parser.finishRelation();
		return nextRec == 0 ? null: new Field(store, nextRec);
	}

	@Override
	public Object java() {
		int field = 0;
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
		case 12:
			return isMandatory();
		case 13:
			return getMinimum();
		case 14:
			return getMaximum();
		case 15:
			return getFormat();
		case 16:
			return getDecimals();
		case 17:
			return getDefault();
		case 18:
			return getCondition();
		case 19:
			return getDescription();
		default:
			return null;
		}
	}

	@Override
	public FieldType type() {
		int field = 0;
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
		case 12:
			return FieldType.BOOLEAN;
		case 13:
			return FieldType.LONG;
		case 14:
			return FieldType.LONG;
		case 15:
			return FieldType.STRING;
		case 16:
			return FieldType.INTEGER;
		case 17:
			return FieldType.STRING;
		case 18:
			return FieldType.STRING;
		case 19:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	@Override
	public String name() {
		int field = 0;
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
		case 12:
			return "mandatory";
		case 13:
			return "minimum";
		case 14:
			return "maximum";
		case 15:
			return "format";
		case 16:
			return "decimals";
		case 17:
			return "default";
		case 18:
			return "condition";
		case 19:
			return "description";
		default:
			return null;
		}
	}

	@Override
	public Field next() {
		return null;
	}

	@Override
	public Field copy() {
		return new Field(store, rec);
	}

	@Override
	public Field copy(int rec) {
		assert store.validate(rec);
		return new Field(store, rec);
	}
}
