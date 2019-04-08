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
 * Automatically generated record class for table MatchStep
 */
@RecordData(name = "MatchStep")
public interface MatchStep extends MemoryRecord, RecordInterface {
	int matchstepPosition();

	@Override
	Store store();

	default ChangeMatchStep changeMatchStep() {
		if (this instanceof MatchingArray)
			return (MatchingArray) this;
		return null;
	}

	public enum Type {
		STACK, PARM, FIELD, ALT, CALL, JUMP, TEST_CALL, TEST_STACK, TEST_BOOLEAN, TEST_STRING, TEST_NUMBER, TEST_FLOAT, TEST_TYPE, TEST_PARM, MATCH_STRING, PUSH, POP, VAR_WRITE, VAR_START, VAR_ADD, ERROR, STEP;

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

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "STACK", "PARM", "FIELD", "ALT", "CALL", "JUMP", "TEST_CALL", "TEST_STACK", "TEST_BOOLEAN", "TEST_STRING", "TEST_NUMBER", "TEST_FLOAT", "TEST_TYPE", "TEST_PARM", "MATCH_STRING", "PUSH", "POP", "VAR_WRITE", "VAR_START", "VAR_ADD", "ERROR", "STEP" }, condition = true, mandatory = false)
	default Type getType() {
		int data = rec() == 0 ? 0 : store().getByte(rec(), matchstepPosition() + 0) & 63;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(name = "stack", type = "INTEGER", when = "STACK", mandatory = false)
	default int getStack() {
		return getType() != Type.STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "pointer", type = "INTEGER", when = "STACK", mandatory = false)
	default int getPointer() {
		return getType() != Type.STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "parm", type = "INTEGER", when = "PARM", mandatory = false)
	default int getParm() {
		return getType() != Type.PARM ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "pfalse", type = "INTEGER", when = "PARM", mandatory = false)
	default int getPfalse() {
		return getType() != Type.PARM ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "field", type = "STRING", when = "FIELD", mandatory = false)
	default String getField() {
		return getType() != Type.FIELD ? null : store().getString(store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "ffalse", type = "INTEGER", when = "FIELD", mandatory = false)
	default int getFfalse() {
		return getType() != Type.FIELD ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "altnr", type = "INTEGER", when = "ALT", mandatory = false)
	default int getAltnr() {
		return getType() != Type.ALT ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "afalse", type = "INTEGER", when = "ALT", mandatory = false)
	default int getAfalse() {
		return getType() != Type.ALT ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "avar", type = "OBJECT", related = Variable.class, when = "ALT", mandatory = false)
	default Variable getAvar() {
		return new Variable(store(), getType() != Type.ALT ? 0 : store().getInt(rec(), matchstepPosition() + 9));
	}

	@FieldData(name = "macro", type = "RELATION", related = Macro.class, when = "CALL", mandatory = false)
	default Macro getMacro() {
		return new Macro(store(), getType() != Type.CALL ? 0 : store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "parms", type = "ARRAY", related = ParmsArray.class, when = "CALL", mandatory = false)
	default ParmsArray getParms() {
		return getType() != Type.CALL ? null : new ParmsArray(this, -1);
	}

	default ParmsArray getParms(int index) {
		return getType() != Type.CALL ? new ParmsArray(store(), 0, -1) : new ParmsArray(this, index);
	}

	default ParmsArray addParms() {
		return getType() != Type.CALL ? new ParmsArray(store(), 0, -1) : getParms().add();
	}

	@FieldData(name = "mfalse", type = "INTEGER", when = "CALL", mandatory = false)
	default int getMfalse() {
		return getType() != Type.CALL ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 9);
	}

	@FieldData(name = "tfalse", type = "INTEGER", when = "TEST_CALL", mandatory = false)
	default int getTfalse() {
		return getType() != Type.TEST_CALL ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "tstack", type = "INTEGER", when = "TEST_STACK", mandatory = false)
	default int getTstack() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "tsfalse", type = "INTEGER", when = "TEST_STACK", mandatory = false)
	default int getTsfalse() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	public enum Jump {
		CONTINUE, CONDITIONAL, MISSED, CALL, RETURN, COMPLETE, MISS, INCOMPLETE;

		private static Map<String, Jump> map = new HashMap<>();

		static {
			for (Jump tp : Jump.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Jump get(String value) {
			return map.get(value);
		}
	}

	@FieldData(name = "jump", type = "ENUMERATE", enumerate = { "CONTINUE", "CONDITIONAL", "MISSED", "CALL", "RETURN", "COMPLETE", "MISS", "INCOMPLETE" }, when = "JUMP", mandatory = false)
	default Jump getJump() {
		int data = getType() != Type.JUMP ? 0 : store().getByte(rec(), matchstepPosition() + 1) & 31;
		if (data <= 0)
			return null;
		return Jump.values()[data - 1];
	}

	@FieldData(name = "position", type = "INTEGER", when = "JUMP", mandatory = false)
	default int getPosition() {
		return getType() != Type.JUMP ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 2);
	}

	@FieldData(name = "mboolean", type = "BOOLEAN", when = "TEST_BOOLEAN", mandatory = false)
	default boolean isMboolean() {
		return getType() != Type.TEST_BOOLEAN ? false : (store().getByte(rec(), matchstepPosition() + 1) & 1) > 0;
	}

	@FieldData(name = "mbfalse", type = "INTEGER", when = "TEST_BOOLEAN", mandatory = false)
	default int getMbfalse() {
		return getType() != Type.TEST_BOOLEAN ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 2);
	}

	@FieldData(name = "tstring", type = "STRING", when = "TEST_STRING", mandatory = false)
	default String getTstring() {
		return getType() != Type.TEST_STRING ? null : store().getString(store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "mtsfalse", type = "INTEGER", when = "TEST_STRING", mandatory = false)
	default int getMtsfalse() {
		return getType() != Type.TEST_STRING ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "mstring", type = "STRING", when = "MATCH_STRING", mandatory = false)
	default String getMstring() {
		return getType() != Type.MATCH_STRING ? null : store().getString(store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "msfalse", type = "INTEGER", when = "MATCH_STRING", mandatory = false)
	default int getMsfalse() {
		return getType() != Type.MATCH_STRING ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "mnumber", type = "LONG", when = "TEST_NUMBER", mandatory = false)
	default long getMnumber() {
		return getType() != Type.TEST_NUMBER ? Long.MIN_VALUE : store().getLong(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "mnfalse", type = "INTEGER", when = "TEST_NUMBER", mandatory = false)
	default int getMnfalse() {
		return getType() != Type.TEST_NUMBER ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 9);
	}

	@FieldData(name = "mfloat", type = "FLOAT", when = "TEST_FLOAT", mandatory = false)
	default double getMfloat() {
		return getType() != Type.TEST_FLOAT ? Double.NaN : Double.longBitsToDouble(store().getLong(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "mffalse", type = "INTEGER", when = "TEST_FLOAT", mandatory = false)
	default int getMffalse() {
		return getType() != Type.TEST_FLOAT ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 9);
	}

	public enum Ttype {
		TYPE_NULL, TYPE_BOOLEAN, TYPE_STRING, TYPE_NUMBER, TYPE_FLOAT, TYPE_ARRAY, TYPE_OBJECT, SKIP;

		private static Map<String, Ttype> map = new HashMap<>();

		static {
			for (Ttype tp : Ttype.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Ttype get(String value) {
			return map.get(value);
		}
	}

	@FieldData(name = "ttype", type = "ENUMERATE", enumerate = { "TYPE_NULL", "TYPE_BOOLEAN", "TYPE_STRING", "TYPE_NUMBER", "TYPE_FLOAT", "TYPE_ARRAY", "TYPE_OBJECT", "SKIP" }, when = "TEST_TYPE", mandatory = false)
	default Ttype getTtype() {
		int data = getType() != Type.TEST_TYPE ? 0 : store().getByte(rec(), matchstepPosition() + 1) & 31;
		if (data <= 0)
			return null;
		return Ttype.values()[data - 1];
	}

	@FieldData(name = "ttfalse", type = "INTEGER", when = "TEST_TYPE", mandatory = false)
	default int getTtfalse() {
		return getType() != Type.TEST_TYPE ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 2);
	}

	@FieldData(name = "tparm", type = "INTEGER", when = "TEST_PARM", mandatory = false)
	default int getTparm() {
		return getType() != Type.TEST_PARM ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "tpfalse", type = "INTEGER", when = "TEST_PARM", mandatory = false)
	default int getTpfalse() {
		return getType() != Type.TEST_PARM ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "popread", type = "BOOLEAN", when = "POP", mandatory = false)
	default boolean isPopread() {
		return getType() != Type.POP ? false : (store().getByte(rec(), matchstepPosition() + 1) & 1) > 0;
	}

	@FieldData(name = "vwrite", type = "OBJECT", related = Variable.class, when = "VAR_WRITE", mandatory = false)
	default Variable getVwrite() {
		return new Variable(store(), getType() != Type.VAR_WRITE ? 0 : store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "vwrange", type = "INTEGER", when = "VAR_WRITE", mandatory = false)
	default int getVwrange() {
		return getType() != Type.VAR_WRITE ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "vstart", type = "OBJECT", related = Variable.class, when = "VAR_START", mandatory = false)
	default Variable getVstart() {
		return new Variable(store(), getType() != Type.VAR_START ? 0 : store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "vadd", type = "OBJECT", related = Variable.class, when = "VAR_ADD", mandatory = false)
	default Variable getVadd() {
		return new Variable(store(), getType() != Type.VAR_ADD ? 0 : store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "varange", type = "INTEGER", when = "VAR_ADD", mandatory = false)
	default int getVarange() {
		return getType() != Type.VAR_ADD ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "error", type = "STRING", when = "ERROR", mandatory = false)
	default String getError() {
		return getType() != Type.ERROR ? null : store().getString(store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "erange", type = "INTEGER", when = "ERROR", mandatory = false)
	default int getErange() {
		return getType() != Type.ERROR ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	public enum Step {
		START, FORWARD, BACK, FINISH;

		private static Map<String, Step> map = new HashMap<>();

		static {
			for (Step tp : Step.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Step get(String value) {
			return map.get(value);
		}
	}

	@FieldData(name = "step", type = "ENUMERATE", enumerate = { "START", "FORWARD", "BACK", "FINISH" }, when = "STEP", mandatory = false)
	default Step getStep() {
		int data = getType() != Type.STEP ? 0 : store().getByte(rec(), matchstepPosition() + 1) & 15;
		if (data <= 0)
			return null;
		return Step.values()[data - 1];
	}

	@FieldData(name = "missed", type = "INTEGER", when = "STEP", mandatory = false)
	default int getMissed() {
		return getType() != Type.STEP ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 2);
	}

	default void outputMatchStep(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		write.field("stack", getStack());
		write.field("pointer", getPointer());
		write.field("parm", getParm());
		write.field("pfalse", getPfalse());
		write.field("field", getField());
		write.field("ffalse", getFfalse());
		write.field("altnr", getAltnr());
		write.field("afalse", getAfalse());
		Variable fldAvar = getAvar();
		if (fldAvar != null && fldAvar.rec() != 0) {
			write.sub("avar");
			fldAvar.output(write, iterate);
			write.endSub();
		}
		write.field("macro", getMacro());
		ParmsArray fldParms = getParms();
		if (fldParms != null) {
			write.sub("parms");
			for (ParmsArray sub : fldParms)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("mfalse", getMfalse());
		write.field("tfalse", getTfalse());
		write.field("tstack", getTstack());
		write.field("tsfalse", getTsfalse());
		write.field("jump", getJump());
		write.field("position", getPosition());
		if (getType() == Type.TEST_BOOLEAN)
			write.field("mboolean", isMboolean());
		write.field("mbfalse", getMbfalse());
		write.field("tstring", getTstring());
		write.field("mtsfalse", getMtsfalse());
		write.field("mstring", getMstring());
		write.field("msfalse", getMsfalse());
		write.field("mnumber", getMnumber());
		write.field("mnfalse", getMnfalse());
		write.field("mfloat", getMfloat());
		write.field("mffalse", getMffalse());
		write.field("ttype", getTtype());
		write.field("ttfalse", getTtfalse());
		write.field("tparm", getTparm());
		write.field("tpfalse", getTpfalse());
		if (getType() == Type.POP)
			write.field("popread", isPopread());
		Variable fldVwrite = getVwrite();
		if (fldVwrite != null && fldVwrite.rec() != 0) {
			write.sub("vwrite");
			fldVwrite.output(write, iterate);
			write.endSub();
		}
		write.field("vwrange", getVwrange());
		Variable fldVstart = getVstart();
		if (fldVstart != null && fldVstart.rec() != 0) {
			write.sub("vstart");
			fldVstart.output(write, iterate);
			write.endSub();
		}
		Variable fldVadd = getVadd();
		if (fldVadd != null && fldVadd.rec() != 0) {
			write.sub("vadd");
			fldVadd.output(write, iterate);
			write.endSub();
		}
		write.field("varange", getVarange());
		write.field("error", getError());
		write.field("erange", getErange());
		write.field("step", getStep());
		write.field("missed", getMissed());
	}

	default Object getMatchStep(int field) {
		switch (field) {
		case 1:
			return getType();
		case 2:
			return getStack();
		case 3:
			return getPointer();
		case 4:
			return getParm();
		case 5:
			return getPfalse();
		case 6:
			return getField();
		case 7:
			return getFfalse();
		case 8:
			return getAltnr();
		case 9:
			return getAfalse();
		case 10:
			return getAvar();
		case 11:
			return getMacro();
		case 13:
			return getMfalse();
		case 14:
			return getTfalse();
		case 15:
			return getTstack();
		case 16:
			return getTsfalse();
		case 17:
			return getJump();
		case 18:
			return getPosition();
		case 19:
			return isMboolean();
		case 20:
			return getMbfalse();
		case 21:
			return getTstring();
		case 22:
			return getMtsfalse();
		case 23:
			return getMstring();
		case 24:
			return getMsfalse();
		case 25:
			return getMnumber();
		case 26:
			return getMnfalse();
		case 27:
			return getMfloat();
		case 28:
			return getMffalse();
		case 29:
			return getTtype();
		case 30:
			return getTtfalse();
		case 31:
			return getTparm();
		case 32:
			return getTpfalse();
		case 33:
			return isPopread();
		case 34:
			return getVwrite();
		case 35:
			return getVwrange();
		case 36:
			return getVstart();
		case 37:
			return getVadd();
		case 38:
			return getVarange();
		case 39:
			return getError();
		case 40:
			return getErange();
		case 41:
			return getStep();
		case 42:
			return getMissed();
		default:
			return null;
		}
	}

	default FieldType typeMatchStep(int field) {
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.INTEGER;
		case 3:
			return FieldType.INTEGER;
		case 4:
			return FieldType.INTEGER;
		case 5:
			return FieldType.INTEGER;
		case 6:
			return FieldType.STRING;
		case 7:
			return FieldType.INTEGER;
		case 8:
			return FieldType.INTEGER;
		case 9:
			return FieldType.INTEGER;
		case 10:
			return FieldType.OBJECT;
		case 11:
			return FieldType.OBJECT;
		case 12:
			return FieldType.ARRAY;
		case 13:
			return FieldType.INTEGER;
		case 14:
			return FieldType.INTEGER;
		case 15:
			return FieldType.INTEGER;
		case 16:
			return FieldType.INTEGER;
		case 17:
			return FieldType.STRING;
		case 18:
			return FieldType.INTEGER;
		case 19:
			return FieldType.BOOLEAN;
		case 20:
			return FieldType.INTEGER;
		case 21:
			return FieldType.STRING;
		case 22:
			return FieldType.INTEGER;
		case 23:
			return FieldType.STRING;
		case 24:
			return FieldType.INTEGER;
		case 25:
			return FieldType.LONG;
		case 26:
			return FieldType.INTEGER;
		case 27:
			return FieldType.FLOAT;
		case 28:
			return FieldType.INTEGER;
		case 29:
			return FieldType.STRING;
		case 30:
			return FieldType.INTEGER;
		case 31:
			return FieldType.INTEGER;
		case 32:
			return FieldType.INTEGER;
		case 33:
			return FieldType.BOOLEAN;
		case 34:
			return FieldType.OBJECT;
		case 35:
			return FieldType.INTEGER;
		case 36:
			return FieldType.OBJECT;
		case 37:
			return FieldType.OBJECT;
		case 38:
			return FieldType.INTEGER;
		case 39:
			return FieldType.STRING;
		case 40:
			return FieldType.INTEGER;
		case 41:
			return FieldType.STRING;
		case 42:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	default String nameMatchStep(int field) {
		switch (field) {
		case 1:
			return "type";
		case 2:
			return "stack";
		case 3:
			return "pointer";
		case 4:
			return "parm";
		case 5:
			return "pfalse";
		case 6:
			return "field";
		case 7:
			return "ffalse";
		case 8:
			return "altnr";
		case 9:
			return "afalse";
		case 10:
			return "avar";
		case 11:
			return "macro";
		case 12:
			return "parms";
		case 13:
			return "mfalse";
		case 14:
			return "tfalse";
		case 15:
			return "tstack";
		case 16:
			return "tsfalse";
		case 17:
			return "jump";
		case 18:
			return "position";
		case 19:
			return "mboolean";
		case 20:
			return "mbfalse";
		case 21:
			return "tstring";
		case 22:
			return "mtsfalse";
		case 23:
			return "mstring";
		case 24:
			return "msfalse";
		case 25:
			return "mnumber";
		case 26:
			return "mnfalse";
		case 27:
			return "mfloat";
		case 28:
			return "mffalse";
		case 29:
			return "ttype";
		case 30:
			return "ttfalse";
		case 31:
			return "tparm";
		case 32:
			return "tpfalse";
		case 33:
			return "popread";
		case 34:
			return "vwrite";
		case 35:
			return "vwrange";
		case 36:
			return "vstart";
		case 37:
			return "vadd";
		case 38:
			return "varange";
		case 39:
			return "error";
		case 40:
			return "erange";
		case 41:
			return "step";
		case 42:
			return "missed";
		default:
			return null;
		}
	}
}
