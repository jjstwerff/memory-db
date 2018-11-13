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
 * Automatically generated record class for table Match
 */
@RecordData(name = "Match")
public interface Match extends MemoryRecord, RecordInterface {
	int matchPosition();

	@Override
	Store getStore();

	boolean parseKey(Parser parser);

	default ChangeMatch changeMatch() {
		if (this instanceof MobjectArray)
			return (MobjectArray) this;
		if (this instanceof ParametersArray)
			return (ParametersArray) this;
		if (this instanceof MarrayArray)
			return (MarrayArray) this;
		return null;
	}

	public enum Type {
		ARRAY, BOOLEAN, NULL, VARIABLE, FLOAT, NUMBER, STRING, OBJECT;

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
		enumerate = {"ARRAY", "BOOLEAN", "NULL", "VARIABLE", "FLOAT", "NUMBER", "STRING", "OBJECT"},
		condition = true,
		mandatory = false
	)
	default Type getType() {
		int data = getRec() == 0 ? 0 : getStore().getByte(getRec(), matchPosition() + 0) & 31;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "marray",
		type = "ARRAY",
		related = MarrayArray.class,
		when = "ARRAY",
		mandatory = false
	)
	default MarrayArray getMarray() {
		return getType() != Type.ARRAY ? null : new MarrayArray(this, -1);
	}

	default MarrayArray getMarray(int index) {
		return getType() != Type.ARRAY ? new MarrayArray(getStore(), 0, -1) : new MarrayArray(this, index);
	}

	default MarrayArray addMarray() {
		return getType() != Type.ARRAY ? new MarrayArray(getStore(), 0, -1) : getMarray().add();
	}

	@FieldData(
		name = "variable",
		type = "OBJECT",
		related = Variable.class,
		when = "VARIABLE",
		mandatory = false
	)
	default Variable getVariable() {
		return new Variable(getStore(), getType() != Type.VARIABLE ? 0 : getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "boolean",
		type = "BOOLEAN",
		when = "BOOLEAN",
		mandatory = false
	)
	default boolean isBoolean() {
		return getType() != Type.BOOLEAN ? false : (getStore().getByte(getRec(), matchPosition() + 1) & 1) > 0;
	}

	@FieldData(
		name = "float",
		type = "FLOAT",
		when = "FLOAT",
		mandatory = false
	)
	default double getFloat() {
		return getType() != Type.FLOAT ? Double.NaN : Double.longBitsToDouble(getStore().getLong(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "number",
		type = "LONG",
		when = "NUMBER",
		mandatory = false
	)
	default long getNumber() {
		return getType() != Type.NUMBER ? Long.MIN_VALUE : getStore().getLong(getRec(), matchPosition() + 1);
	}

	@FieldData(
		name = "string",
		type = "STRING",
		when = "STRING",
		mandatory = false
	)
	default String getString() {
		return getType() != Type.STRING ? null : getStore().getString(getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "mobject",
		type = "ARRAY",
		related = MobjectArray.class,
		when = "OBJECT",
		mandatory = false
	)
	default MobjectArray getMobject() {
		return getType() != Type.OBJECT ? null : new MobjectArray(this, -1);
	}

	default MobjectArray getMobject(int index) {
		return getType() != Type.OBJECT ? new MobjectArray(getStore(), 0, -1) : new MobjectArray(this, index);
	}

	default MobjectArray addMobject() {
		return getType() != Type.OBJECT ? new MobjectArray(getStore(), 0, -1) : getMobject().add();
	}

	default void outputMatch(Write write, int iterate, boolean first) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
		write.field("type", getType(), first);
		MarrayArray fldMarray = getMarray();
		if (fldMarray != null) {
			write.sub("marray", false);
			for (MarrayArray sub : fldMarray)
				sub.output(write, iterate);
			write.endSub();
		}
		Variable fldVariable = getVariable();
		if (fldVariable != null && fldVariable.getRec() != 0) {
			write.sub("variable", false);
			fldVariable.output(write, iterate);
			write.endSub();
		}
		if (getType() == Type.BOOLEAN)
			write.field("boolean", isBoolean(), false);
		write.field("float", getFloat(), false);
		write.field("number", getNumber(), false);
		write.field("string", getString(), false);
		MobjectArray fldMobject = getMobject();
		if (fldMobject != null) {
			write.sub("mobject", false);
			for (MobjectArray sub : fldMobject)
				sub.output(write, iterate);
			write.endSub();
		}
	}

	default Object getMatch(int field) {
		switch (field) {
		case 1:
			return getType();
		case 3:
			return getVariable();
		case 4:
			return isBoolean();
		case 5:
			return getFloat();
		case 6:
			return getNumber();
		case 7:
			return getString();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateMatch(int field, @SuppressWarnings("unused") Object... key) {
		switch (field) {
		case 2:
			return getMarray();
		case 8:
			return getMobject();
		default:
			return null;
		}
	}

	default FieldType typeMatch(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.ARRAY;
		case 3:
			return FieldType.OBJECT;
		case 4:
			return FieldType.BOOLEAN;
		case 5:
			return FieldType.FLOAT;
		case 6:
			return FieldType.LONG;
		case 7:
			return FieldType.STRING;
		case 8:
			return FieldType.ARRAY;
		default:
			return null;
		}
	}

	default String nameMatch(int field) {
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "marray";
		case 3:
			return "variable";
		case 4:
			return "boolean";
		case 5:
			return "float";
		case 6:
			return "number";
		case 7:
			return "string";
		case 8:
			return "mobject";
		default:
			return null;
		}
	}
}
