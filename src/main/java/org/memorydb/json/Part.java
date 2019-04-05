package org.memorydb.json;

import java.util.Iterator;

import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.IndexOperation;
import org.memorydb.structure.Key;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RedBlackTree;
import org.memorydb.structure.TreeIndex;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table Part
 */
@RecordData(name = "Part")
public interface Part extends MemoryRecord, RecordInterface {
	int partPosition();

	@Override
	Store store();

	default ChangePart changePart() {
		if (this instanceof Field)
			return new ChangeField((Field) this);
		if (this instanceof ArrayArray)
			return (ArrayArray) this;
		if (this instanceof Json)
			return new ChangeJson((Json) this);
		return null;
	}

	public enum Type {
		ARRAY, BOOLEAN, FLOAT, NUMBER, NULL, OBJECT, STRING;

		private static Map<String, Type> map = new HashMap<>();

		static {
			for (Type tp : Type.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Type get(String value) {
			return map.get(value);
		}
	}

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING" }, condition = true, mandatory = false)
	default Type getType() {
		int data = rec() == 0 ? 0 : store().getByte(rec(), partPosition() + 0) & 31;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "array", type = "ARRAY", related = ArrayArray.class, when = "ARRAY", mandatory = false)
	default ArrayArray getArray() {
		return getType() != Type.ARRAY ? null : new ArrayArray(this, -1);
	}

	default ArrayArray getArray(int index) {
		return getType() != Type.ARRAY ? new ArrayArray(store(), 0, -1) : new ArrayArray(this, index);
	}

	default ArrayArray addArray() {
		return getType() != Type.ARRAY ? new ArrayArray(store(), 0, -1) : getArray().add();
	}

	@FieldData(name = "boolean", type = "BOOLEAN", when = "BOOLEAN", mandatory = false)
	default boolean isBoolean() {
		return getType() != Type.BOOLEAN ? false : (store().getByte(rec(), partPosition() + 1) & 1) > 0;
	}

	@FieldData(name = "float", type = "FLOAT", when = "FLOAT", mandatory = false)
	default double getFloat() {
		return getType() != Type.FLOAT ? Double.NaN : Double.longBitsToDouble(store().getLong(rec(), partPosition() + 1));
	}

	@FieldData(name = "number", type = "LONG", when = "NUMBER", mandatory = false)
	default long getNumber() {
		return getType() != Type.NUMBER ? Long.MIN_VALUE : store().getLong(rec(), partPosition() + 1);
	}

	@FieldData(name = "object", type = "SET", related = Field.class, when = "OBJECT", mandatory = false)
	default IndexObject getObject() {
		return getType() != Type.OBJECT ? null : new IndexObject(this);
	}

	default Field getObject(String key1) {
		int res = new IndexObject(this, key1).search();
		return res <= 0 ? null : new Field(store(), res);
	}

	default ChangeField addObject() {
		return new ChangeField(this, 0);
	}

	/* package private */ static class IndexObject extends TreeIndex implements Iterable<Field> {
		private final Part record;

		public IndexObject(Part record) {
			super(record.store(), null, 64, 9);
			this.record = record;
		}

		public IndexObject(Part record, String key1) {
			super(record.store(), new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert record.store().validate(recNr);
					Field rec = new Field(record.store(), recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, rec.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 64, 9);
			this.record = record;
		}

		@Override
		public int first() {
			return super.first();
		}

		@Override
		public int next(int val) {
			return super.next(val);
		}

		@Override
		protected int readTop() {
			return record.store().getInt(record.rec(), record.partPosition() + 1);
		}

		@Override
		protected void changeTop(int value) {
			record.store().setInt(record.rec(), record.partPosition() + 1, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Field recA = new Field(record.store(), a);
			Field recB = new Field(record.store(), b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}

		@Override
		public Iterator<Field> iterator() {
			return new Iterator<>() {
				int nextRec = search();

				@Override
				public boolean hasNext() {
					return nextRec > 0;
				}

				@Override
				public Field next() {
					int n = nextRec;
					nextRec = toNext(nextRec);
					return n <= 0 ? null : new Field(store, n);
				}
			};
		}
	}

	@FieldData(name = "value", type = "STRING", when = "STRING", mandatory = false)
	default String getValue() {
		return getType() != Type.STRING ? null : store().getString(store().getInt(rec(), partPosition() + 1));
	}

	default void outputPart(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		ArrayArray fldArray = getArray();
		if (fldArray != null) {
			write.sub("array");
			for (ArrayArray sub : fldArray)
				sub.output(write, iterate);
			write.endSub();
		}
		if (getType() == Type.BOOLEAN)
			write.field("boolean", isBoolean());
		write.field("float", getFloat());
		write.field("number", getNumber());
		IndexObject fldObject = getObject();
		if (fldObject != null) {
			write.sub("object");
			for (Field sub : fldObject)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("value", getValue());
	}

	@Override
	default Object java() {
		Type type = getType();
		if (type == null)
			return null;
		switch (type) {
		case ARRAY:
			return this instanceof ArrayArray ? new ArrayArray(this, -1) : this;
		case BOOLEAN:
			return isBoolean();
		case FLOAT:
			return getFloat();
		case NULL:
			return null;
		case NUMBER:
			return getNumber();
		case OBJECT:
			return this;
		case STRING:
			return getValue();
		default:
			return null;
		}
	}

	@Override
	default Part start() {
		Type type = getType();
		if (type == null)
			return null;
		switch (type) {
		case ARRAY:
			ArrayArray elm = new ArrayArray(this, 0);
			return  elm.size() == 0 ? null : elm;
		case OBJECT:
			return new Field(store(), new IndexObject(this).first());
		default:
			return null;
		}
	}

	@Override
	default Part next() {
		Part parent = (Part) up();
		if (parent == null)
			return null;
		Type type = parent.getType();
		if (type == null)
			return null;
		switch (type) {
		case ARRAY:
			if (!(this instanceof ArrayArray))
				throw new RuntimeException("Incorrect current type");
			ArrayArray cur = (ArrayArray) this;
			int idx = cur.index();
			if (idx + 1 >= cur.size())
				return null;
			return new ArrayArray(parent, idx + 1);
		case OBJECT:
			int pos = rec();
			if (pos < 0)
				return null;
			int next = new IndexObject(parent).next(pos);
			return next > 0 ? new Field(store(), next) : null;
		default:
			return null;
		}
	}

	default FieldType getFieldType() {
		Type type = getType();
		if (type == null)
			return null;
		switch (type) {
		case ARRAY:
			return FieldType.ARRAY;
		case BOOLEAN:
			return FieldType.BOOLEAN;
		case FLOAT:
			return FieldType.FLOAT;
		case NULL:
			return FieldType.NULL;
		case NUMBER:
			return FieldType.LONG;
		case OBJECT:
			return FieldType.OBJECT;
		case STRING:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	default ArrayArray index(int index) {
		if (index < 0 || index >= size() || getType() != Type.ARRAY)
			return null;
		return new ArrayArray(this, index);
	}

	default RecordInterface field(String name) {
		if (name == null || getType() != Type.OBJECT)
			return null;
		int pos = new IndexObject(this, name).search();
		return pos <= 0 ? null : new Field(store(), pos);
	}

	@Override
	default int size() {
		if (getType() != Type.ARRAY)
			return 0;
		ArrayArray array = getArray();
		if (array == null)
			return 0;
		return array.size();
	}
}
