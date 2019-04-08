package org.memorydb.jslt;

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
	Store store();

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

	@FieldData(name = "variable", type = "OBJECT", related = Variable.class, mandatory = false)
	default Variable getVariable() {
		return new Variable(store(), rec() == 0 ? 0 : store().getInt(rec(), matchPosition() + 0));
	}

	public enum Type {
		ANY, ARRAY, BOOLEAN, NULL, FLOAT, NUMBER, STRING, OBJECT, CONSTANT, MACRO, MULTIPLE;

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

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "ANY", "ARRAY", "BOOLEAN", "NULL", "FLOAT", "NUMBER", "STRING", "OBJECT", "CONSTANT", "MACRO", "MULTIPLE" }, condition = true, mandatory = false)
	default Type getType() {
		int data = rec() == 0 ? 0 : store().getByte(rec(), matchPosition() + 4) & 31;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "marray", type = "ARRAY", related = MarrayArray.class, when = "ARRAY", mandatory = false)
	default MarrayArray getMarray() {
		return getType() != Type.ARRAY ? null : new MarrayArray(this, -1);
	}

	default MarrayArray getMarray(int index) {
		return getType() != Type.ARRAY ? new MarrayArray(store(), 0, -1) : new MarrayArray(this, index);
	}

	default MarrayArray addMarray() {
		return getType() != Type.ARRAY ? new MarrayArray(store(), 0, -1) : getMarray().add();
	}

	@FieldData(name = "boolean", type = "BOOLEAN", when = "BOOLEAN", mandatory = false)
	default boolean isBoolean() {
		return getType() != Type.BOOLEAN ? false : (store().getByte(rec(), matchPosition() + 5) & 1) > 0;
	}

	@FieldData(name = "float", type = "FLOAT", when = "FLOAT", mandatory = false)
	default double getFloat() {
		return getType() != Type.FLOAT ? Double.NaN : Double.longBitsToDouble(store().getLong(rec(), matchPosition() + 5));
	}

	@FieldData(name = "number", type = "LONG", when = "NUMBER", mandatory = false)
	default long getNumber() {
		return getType() != Type.NUMBER ? Long.MIN_VALUE : store().getLong(rec(), matchPosition() + 5);
	}

	@FieldData(name = "string", type = "STRING", when = "STRING", mandatory = false)
	default String getString() {
		return getType() != Type.STRING ? null : store().getString(store().getInt(rec(), matchPosition() + 5));
	}

	@FieldData(name = "mobject", type = "ARRAY", related = MobjectArray.class, when = "OBJECT", mandatory = false)
	default MobjectArray getMobject() {
		return getType() != Type.OBJECT ? null : new MobjectArray(this, -1);
	}

	default MobjectArray getMobject(int index) {
		return getType() != Type.OBJECT ? new MobjectArray(store(), 0, -1) : new MobjectArray(this, index);
	}

	default MobjectArray addMobject() {
		return getType() != Type.OBJECT ? new MobjectArray(store(), 0, -1) : getMobject().add();
	}

	@FieldData(name = "cparm", type = "STRING", when = "CONSTANT", mandatory = false)
	default String getCparm() {
		return getType() != Type.CONSTANT ? null : store().getString(store().getInt(rec(), matchPosition() + 5));
	}

	@FieldData(name = "constant", type = "INTEGER", when = "CONSTANT", mandatory = false)
	default int getConstant() {
		return getType() != Type.CONSTANT ? Integer.MIN_VALUE : store().getInt(rec(), matchPosition() + 9);
	}

	@FieldData(name = "macro", type = "RELATION", related = Macro.class, when = "MACRO", mandatory = false)
	default Macro getMacro() {
		return new Macro(store(), getType() != Type.MACRO ? 0 : store().getInt(rec(), matchPosition() + 5));
	}

	@FieldData(name = "mparms", type = "ARRAY", related = MparmsArray.class, when = "MACRO", mandatory = false)
	default MparmsArray getMparms() {
		return getType() != Type.MACRO ? null : new MparmsArray(this, -1);
	}

	default MparmsArray getMparms(int index) {
		return getType() != Type.MACRO ? new MparmsArray(store(), 0, -1) : new MparmsArray(this, index);
	}

	default MparmsArray addMparms() {
		return getType() != Type.MACRO ? new MparmsArray(store(), 0, -1) : getMparms().add();
	}

	@FieldData(name = "mmatch", type = "OBJECT", related = MatchObject.class, when = "MULTIPLE", mandatory = false)
	default MatchObject getMmatch() {
		return new MatchObject(store(), getType() != Type.MULTIPLE ? 0 : store().getInt(rec(), matchPosition() + 5));
	}

	@FieldData(name = "mmin", type = "BYTE", when = "MULTIPLE", mandatory = false)
	default byte getMmin() {
		return getType() != Type.MULTIPLE ? 0 : store().getByte(rec(), matchPosition() + 9);
	}

	@FieldData(name = "mmax", type = "BYTE", when = "MULTIPLE", mandatory = false)
	default byte getMmax() {
		return getType() != Type.MULTIPLE ? 0 : store().getByte(rec(), matchPosition() + 10);
	}

	default void outputMatch(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
		Variable fldVariable = getVariable();
		if (fldVariable != null && fldVariable.rec() != 0) {
			write.sub("variable");
			fldVariable.output(write, iterate);
			write.endSub();
		}
		write.field("type", getType());
		MarrayArray fldMarray = getMarray();
		if (fldMarray != null) {
			write.sub("marray");
			for (MarrayArray sub : fldMarray)
				sub.output(write, iterate);
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
		if (fldMmatch != null && fldMmatch.rec() != 0) {
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
			return getVariable();
		case 2:
			return getType();
		case 4:
			return isBoolean();
		case 5:
			return getFloat();
		case 6:
			return getNumber();
		case 7:
			return getString();
		case 9:
			return getCparm();
		case 10:
			return getConstant();
		case 11:
			return getMacro();
		case 13:
			return getMmatch();
		case 14:
			return getMmin();
		case 15:
			return getMmax();
		default:
			return null;
		}
	}

	default FieldType typeMatch(int field) {
		switch (field) {
		case 1:
			return FieldType.OBJECT;
		case 2:
			return FieldType.STRING;
		case 3:
			return FieldType.ARRAY;
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
		case 9:
			return FieldType.STRING;
		case 10:
			return FieldType.INTEGER;
		case 11:
			return FieldType.OBJECT;
		case 12:
			return FieldType.ARRAY;
		case 13:
			return FieldType.OBJECT;
		case 14:
			return FieldType.INTEGER;
		case 15:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	default String nameMatch(int field) {
		switch (field) {
		case 1:
			return "variable";
		case 2:
			return "type";
		case 3:
			return "marray";
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
		case 9:
			return "cparm";
		case 10:
			return "constant";
		case 11:
			return "macro";
		case 12:
			return "mparms";
		case 13:
			return "mmatch";
		case 14:
			return "mmin";
		case 15:
			return "mmax";
		default:
			return null;
		}
	}
}
