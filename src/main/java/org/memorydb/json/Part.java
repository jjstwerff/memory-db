package org.memorydb.json;

import java.io.IOException;

import org.memorydb.file.Parser;
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
	Store getStore();

	boolean parseKey(Parser parser);

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

	@FieldData(
		name = "type",
		type = "ENUMERATE",
		enumerate = {"ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING"},
		condition = true,
		mandatory = false
	)
	default Type getType() {
		int data = getRec() == 0 ? 0 : getStore().getByte(getRec(), partPosition() + 0) & 31;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "array",
		type = "ARRAY",
		related = ArrayArray.class,
		when = "ARRAY",
		mandatory = false
	)
	default ArrayArray getArray() {
		return getType() != Type.ARRAY ? null : new ArrayArray(this, -1);
	}

	default ArrayArray getArray(int index) {
		return getType() != Type.ARRAY ? new ArrayArray(getStore(), 0, -1) : this instanceof ArrayArray ? new ArrayArray((ArrayArray)this, index) : new ArrayArray(this, index);
	}

	default ArrayArray addArray() {
		return getType() != Type.ARRAY ? new ArrayArray(getStore(), 0, -1) : getArray().add();
	}

	@FieldData(
		name = "boolean",
		type = "BOOLEAN",
		when = "BOOLEAN",
		mandatory = false
	)
	default boolean isBoolean() {
		return getType() != Type.BOOLEAN ? false : (getStore().getByte(getRec(), partPosition() + 1) & 1) > 0;
	}

	@FieldData(
		name = "float",
		type = "FLOAT",
		when = "FLOAT",
		mandatory = false
	)
	default double getFloat() {
		return getType() != Type.FLOAT ? Double.NaN : Double.longBitsToDouble(getStore().getLong(getRec(), partPosition() + 1));
	}

	@FieldData(
		name = "number",
		type = "LONG",
		when = "NUMBER",
		mandatory = false
	)
	default long getNumber() {
		return getType() != Type.NUMBER ? Long.MIN_VALUE : getStore().getLong(getRec(), partPosition() + 1);
	}

	@FieldData(
		name = "object",
		type = "SET",
		keyNames = {"name"},
		keyTypes = {"STRING"},
		related = Field.class,
		when = "OBJECT",
		mandatory = false
	)
	default IndexObject getObject() {
		return getType() != Type.OBJECT ? null : new IndexObject(this, new Field(getStore()));
	}

	default Field getObject(String key1) {
		Field resultRec = new Field(getStore());
		IndexObject idx = new IndexObject(this, resultRec, key1);
		int res = idx.search();
		if (res == 0)
			return resultRec;
		return new Field(getStore(), res);
	}

	default ChangeField addObject() {
		return new ChangeField(this, 0);
	}

	/* package private */ class IndexObject extends TreeIndex<Field> {
		private final Part part;

		public IndexObject(Part part, Field record) {
			super(record, null, 64, 9);
			this.part = part;
		}

		public IndexObject(Part part, Field record, String key1) {
			super(record, new Key() {
				@Override
				public int compareTo(int recNr) {
					if (recNr < 0)
						return -1;
					assert record.getStore().validate(recNr);
					record.setRec(recNr);
					int o = 0;
					o = RedBlackTree.compare(key1, record.getName());
					return o;
				}

				@Override
				public IndexOperation oper() {
					return IndexOperation.EQ;
				}
			}, 64, 9);
			this.part = part;
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
			return part.getStore().getInt(part.getRec(), part.partPosition() + 1);
		}

		@Override
		protected void changeTop(int value) {
			part.getStore().setInt(part.getRec(), part.partPosition() + 1, value);
		}

		@Override
		protected int compareTo(int a, int b) {
			Field recA = new Field(part.getStore(), a);
			Field recB = new Field(part.getStore(), b);
			int o = 0;
			o = compare(recA.getName(), recB.getName());
			return o;
		}
	}

	@FieldData(
		name = "value",
		type = "STRING",
		when = "STRING",
		mandatory = false
	)
	default String getValue() {
		return getType() != Type.STRING ? null : getStore().getString(getStore().getInt(getRec(), partPosition() + 1));
	}

	default void outputPart(Write write, int iterate) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		ArrayArray fldArray = getArray();
		if (fldArray != null) {
			write.sub("array");
			for (ArrayArray sub : fldArray)
				sub.output(write, iterate - 1);
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
				sub.output(write, iterate - 1);
			write.endSub();
		}
		write.field("value", getValue());
	}

	default Object getContent() {
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
	default int next(int field) {
		if (getType() == Type.ARRAY)
			return field < 0 ? 1 : (field >= getSize() ? -2 : field + 1);
		if (getType() != Type.OBJECT)
			return field == 0 ? -2 : 0;
		if (field < 0)
			return getObject().first();
		int res = getObject().next(field);
		return res <= 0 ? -2 : res;
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

	@Override
	default int getSize() {
		if (getType() != Type.ARRAY)
			return 0;
		ArrayArray array = getArray();
		if (array == null)
			return 0;
		return array.getSize();
	}

	default FieldType typePart() {
		return getFieldType();
	}

	default FieldType typePart(int field) {
		if (field < 0)
			return null;
		if (getType() == Type.ARRAY) {
			if (field == 0)
				return FieldType.ARRAY;
			ArrayArray array = getArray(field - 1);
			return array == null || !array.exists() ? null : array.getFieldType();
		}
		if (getType() != Type.OBJECT && field == 0)
			return getFieldType();
		return new Field(getStore(), field).getFieldType();
	}

	default String namePart(int field) {
		if (field < 0 || getType() != Type.OBJECT)
			return null;
		return new Field(getStore(), field).getName();
	}

	default Object getPart(int field) {
		if (field < 0)
			return null;
		if (getType() == Type.ARRAY)
			return getArray(field - 1).getContent();
		if (getType() != Type.OBJECT)
			return getContent();
		return new Field(getStore(), field).getContent();
	}

	default Iterable<? extends RecordInterface> iteratePart(int field, @SuppressWarnings("unused") Object... key) {
		if (field < 0)
			return null;
		if (getType() == Type.ARRAY)
			return getArray();
		return new Field(getStore(), field).getArray();
	}
}
