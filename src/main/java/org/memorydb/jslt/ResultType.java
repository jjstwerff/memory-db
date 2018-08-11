package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.MemoryRecord;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table ResultType
 */
@RecordData(name = "ResultType")
public interface ResultType extends MemoryRecord, RecordInterface {
	int resulttypePosition();

	@Override
	Store getStore();

	boolean parseKey(Parser parser);

	default ChangeResultType changeResultType() {
		if (this instanceof Variable)
			return new ChangeVariable((Variable) this);
		return null;
	}

	public enum Type {
		ARRAY, BOOLEAN, FLOAT, NUMBER, NULL, OBJECT, STRING, STRUCTURE;

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
		enumerate = {"ARRAY", "BOOLEAN", "FLOAT", "NUMBER", "NULL", "OBJECT", "STRING", "STRUCTURE"},
		mandatory = true
	)
	default Type getType() {
		int data = getRec() == 0 ? 0 : getStore().getByte(getRec(), resulttypePosition() + 0) & 31;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "record",
		type = "STRING",
		mandatory = false
	)
	default String getRecord() {
		return getRec() == 0 ? null : getStore().getString(getStore().getInt(getRec(), resulttypePosition() + 1));
	}

	default void outputResultType(Write write, int iterate, boolean first) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
		write.field("type", getType(), first);
		write.field("record", getRecord(), false);
	}

	default Object getResultType(int field) {
		switch (field) {
		case 1:
			return getType();
		case 2:
			return getRecord();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateResultType(int field, @SuppressWarnings("unused") Object... key) {
		switch (field) {
		default:
			return null;
		}
	}

	default FieldType typeResultType(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.STRING;
		default:
			return null;
		}
	}

	default String nameResultType(int field) {
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "record";
		default:
			return null;
		}
	}
}
