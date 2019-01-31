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
		if (this instanceof MatchObject)
			return new ChangeMatchObject((MatchObject) this);
		if (this instanceof ParametersArray)
			return (ParametersArray) this;
		if (this instanceof MarrayArray)
			return (MarrayArray) this;
		return null;
	}

	public enum Type {
		ARRAY, BOOLEAN, NULL, VARIABLE, FLOAT, NUMBER, STRING, OBJECT, CONSTANT, MACRO, MULTIPLE;

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
		enumerate = {"ARRAY", "BOOLEAN", "NULL", "VARIABLE", "FLOAT", "NUMBER", "STRING", "OBJECT", "CONSTANT", "MACRO", "MULTIPLE"},
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
		name = "vmatch",
		type = "OBJECT",
		related = MatchObject.class,
		when = "VARIABLE",
		mandatory = false
	)
	default MatchObject getVmatch() {
		return new MatchObject(getStore(), getType() != Type.VARIABLE ? 0 : getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "variable",
		type = "OBJECT",
		related = Variable.class,
		when = "VARIABLE",
		mandatory = false
	)
	default Variable getVariable() {
		return new Variable(getStore(), getType() != Type.VARIABLE ? 0 : getStore().getInt(getRec(), matchPosition() + 5));
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

	@FieldData(
		name = "cparm",
		type = "STRING",
		when = "CONSTANT",
		mandatory = false
	)
	default String getCparm() {
		return getType() != Type.CONSTANT ? null : getStore().getString(getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "constant",
		type = "INTEGER",
		when = "CONSTANT",
		mandatory = false
	)
	default int getConstant() {
		return getType() != Type.CONSTANT ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchPosition() + 5);
	}

	@FieldData(
		name = "macro",
		type = "RELATION",
		related = Macro.class,
		when = "MACRO",
		mandatory = false
	)
	default Macro getMacro() {
		return new Macro(getStore(), getType() != Type.MACRO ? 0 : getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "mparms",
		type = "ARRAY",
		related = MparmsArray.class,
		when = "MACRO",
		mandatory = false
	)
	default MparmsArray getMparms() {
		return getType() != Type.MACRO ? null : new MparmsArray(this, -1);
	}

	default MparmsArray getMparms(int index) {
		return getType() != Type.MACRO ? new MparmsArray(getStore(), 0, -1) : new MparmsArray(this, index);
	}

	default MparmsArray addMparms() {
		return getType() != Type.MACRO ? new MparmsArray(getStore(), 0, -1) : getMparms().add();
	}

	@FieldData(
		name = "mmatch",
		type = "OBJECT",
		related = MatchObject.class,
		when = "MULTIPLE",
		mandatory = false
	)
	default MatchObject getMmatch() {
		return new MatchObject(getStore(), getType() != Type.MULTIPLE ? 0 : getStore().getInt(getRec(), matchPosition() + 1));
	}

	@FieldData(
		name = "mmin",
		type = "BYTE",
		when = "MULTIPLE",
		mandatory = false
	)
	default byte getMmin() {
		return getType() != Type.MULTIPLE ? 0 : getStore().getByte(getRec(), matchPosition() + 5);
	}

	@FieldData(
		name = "mmax",
		type = "BYTE",
		when = "MULTIPLE",
		mandatory = false
	)
	default byte getMmax() {
		return getType() != Type.MULTIPLE ? 0 : getStore().getByte(getRec(), matchPosition() + 6);
	}

	default void outputMatch(Write write, int iterate) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		MarrayArray fldMarray = getMarray();
		if (fldMarray != null) {
			write.sub("marray");
			for (MarrayArray sub : fldMarray)
				sub.output(write, iterate);
			write.endSub();
		}
		MatchObject fldVmatch = getVmatch();
		if (fldVmatch != null && fldVmatch.getRec() != 0) {
			write.sub("vmatch");
			fldVmatch.output(write, iterate);
			write.endSub();
		}
		Variable fldVariable = getVariable();
		if (fldVariable != null && fldVariable.getRec() != 0) {
			write.sub("variable");
			fldVariable.output(write, iterate);
			write.endSub();
		}
		if (getType() == Type.BOOLEAN)
			write.field("boolean", isBoolean());
		write.field("float", getFloat());
		write.field("number", getNumber());
		write.field("string", getString());
		MobjectArray fldMobject = getMobject();
		if (fldMobject != null) {
			write.sub("mobject");
			for (MobjectArray sub : fldMobject)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("cparm", getCparm());
		write.field("constant", getConstant());
		write.field("macro", getMacro());
		MparmsArray fldMparms = getMparms();
		if (fldMparms != null) {
			write.sub("mparms");
			for (MparmsArray sub : fldMparms)
				sub.output(write, iterate);
			write.endSub();
		}
		MatchObject fldMmatch = getMmatch();
		if (fldMmatch != null && fldMmatch.getRec() != 0) {
			write.sub("mmatch");
			fldMmatch.output(write, iterate);
			write.endSub();
		}
		if (getType() == Type.MULTIPLE)
			write.field("mmin", getMmin());
		if (getType() == Type.MULTIPLE)
			write.field("mmax", getMmax());
	}

	default Object getMatch(int field) {
		switch (field) {
		case 1:
			return getType();
		case 3:
			return getVmatch();
		case 4:
			return getVariable();
		case 5:
			return isBoolean();
		case 6:
			return getFloat();
		case 7:
			return getNumber();
		case 8:
			return getString();
		case 10:
			return getCparm();
		case 11:
			return getConstant();
		case 12:
			return getMacro();
		case 14:
			return getMmatch();
		case 15:
			return getMmin();
		case 16:
			return getMmax();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateMatch(int field, @SuppressWarnings("unused") Object... key) {
		switch (field) {
		case 2:
			return getMarray();
		case 9:
			return getMobject();
		case 13:
			return getMparms();
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
			return FieldType.OBJECT;
		case 5:
			return FieldType.BOOLEAN;
		case 6:
			return FieldType.FLOAT;
		case 7:
			return FieldType.LONG;
		case 8:
			return FieldType.STRING;
		case 9:
			return FieldType.ARRAY;
		case 10:
			return FieldType.STRING;
		case 11:
			return FieldType.INTEGER;
		case 12:
			return FieldType.OBJECT;
		case 13:
			return FieldType.ARRAY;
		case 14:
			return FieldType.OBJECT;
		case 15:
			return FieldType.INTEGER;
		case 16:
			return FieldType.INTEGER;
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
			return "vmatch";
		case 4:
			return "variable";
		case 5:
			return "boolean";
		case 6:
			return "float";
		case 7:
			return "number";
		case 8:
			return "string";
		case 9:
			return "mobject";
		case 10:
			return "cparm";
		case 11:
			return "constant";
		case 12:
			return "macro";
		case 13:
			return "mparms";
		case 14:
			return "mmatch";
		case 15:
			return "mmin";
		case 16:
			return "mmax";
		default:
			return null;
		}
	}
}
