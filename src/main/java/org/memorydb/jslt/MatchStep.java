package org.memorydb.jslt;

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
 * Automatically generated record class for table MatchStep
 */
@RecordData(name = "MatchStep")
public interface MatchStep extends MemoryRecord, RecordInterface {
	int matchstepPosition();

	@Override
	Store store();

	boolean parseKey(Parser parser);

	default ChangeMatchStep changeMatchStep() {
		if (this instanceof MatchingArray)
			return (MatchingArray) this;
		return null;
	}

	public enum Type {
		STACK, PARM, FIELD, ALT, TEST_CALL, JUMP, TEST_STACK, TEST_BOOLEAN, TEST_STRING, TEST_NUMBER, TEST_FLOAT, TEST_TYPE, PUSH, POP, VAR_WRITE, VAR_START, VAR_ADD, ERROR, START, FINISH;

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

	@FieldData(name = "type", type = "ENUMERATE", enumerate = { "STACK", "PARM", "FIELD", "ALT", "TEST_CALL", "JUMP", "TEST_STACK", "TEST_BOOLEAN", "TEST_STRING", "TEST_NUMBER", "TEST_FLOAT", "TEST_TYPE", "PUSH", "POP", "VAR_WRITE", "VAR_START", "VAR_ADD", "ERROR", "START", "FINISH" }, condition = true, mandatory = false)
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

	@FieldData(name = "tstack", type = "INTEGER", when = "TEST_STACK", mandatory = false)
	default int getTstack() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "tsfalse", type = "INTEGER", when = "TEST_STACK", mandatory = false)
	default int getTsfalse() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "tmacro", type = "RELATION", related = Macro.class, when = "TEST_CALL", mandatory = false)
	default Macro getTmacro() {
		return new Macro(store(), getType() != Type.TEST_CALL ? 0 : store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "tfalse", type = "INTEGER", when = "TEST_CALL", mandatory = false)
	default int getTfalse() {
		return getType() != Type.TEST_CALL ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
	}

	@FieldData(name = "jump", type = "INTEGER", when = "JUMP", mandatory = false)
	default int getJump() {
		return getType() != Type.JUMP ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "mboolean", type = "BOOLEAN", when = "TEST_BOOLEAN", mandatory = false)
	default boolean isMboolean() {
		return getType() != Type.TEST_BOOLEAN ? false : (store().getByte(rec(), matchstepPosition() + 1) & 1) > 0;
	}

	@FieldData(name = "mbfalse", type = "INTEGER", when = "TEST_BOOLEAN", mandatory = false)
	default int getMbfalse() {
		return getType() != Type.TEST_BOOLEAN ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 2);
	}

	@FieldData(name = "mstring", type = "STRING", when = "TEST_STRING", mandatory = false)
	default String getMstring() {
		return getType() != Type.TEST_STRING ? null : store().getString(store().getInt(rec(), matchstepPosition() + 1));
	}

	@FieldData(name = "msfalse", type = "INTEGER", when = "TEST_STRING", mandatory = false)
	default int getMsfalse() {
		return getType() != Type.TEST_STRING ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 5);
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

	@FieldData(name = "notstarted", type = "INTEGER", when = "START", mandatory = false)
	default int getNotstarted() {
		return getType() != Type.START ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	@FieldData(name = "notfinished", type = "INTEGER", when = "FINISH", mandatory = false)
	default int getNotfinished() {
		return getType() != Type.FINISH ? Integer.MIN_VALUE : store().getInt(rec(), matchstepPosition() + 1);
	}

	default void outputMatchStep(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
		write.field("type", getType());
		write.field("stack", getStack());
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
		write.field("tstack", getTstack());
		write.field("tsfalse", getTsfalse());
		write.field("tmacro", getTmacro());
		write.field("tfalse", getTfalse());
		write.field("jump", getJump());
		if (getType() == Type.TEST_BOOLEAN)
			write.field("mboolean", isMboolean());
		write.field("mbfalse", getMbfalse());
		write.field("mstring", getMstring());
		write.field("msfalse", getMsfalse());
		write.field("mnumber", getMnumber());
		write.field("mnfalse", getMnfalse());
		write.field("mfloat", getMfloat());
		write.field("mffalse", getMffalse());
		write.field("ttype", getTtype());
		write.field("ttfalse", getTtfalse());
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
		write.field("notstarted", getNotstarted());
		write.field("notfinished", getNotfinished());
	}

	default Object getMatchStep(int field) {
		switch (field) {
		case 1:
			return getType();
		case 2:
			return getStack();
		case 3:
			return getParm();
		case 4:
			return getPfalse();
		case 5:
			return getField();
		case 6:
			return getFfalse();
		case 7:
			return getAltnr();
		case 8:
			return getAfalse();
		case 9:
			return getAvar();
		case 10:
			return getTstack();
		case 11:
			return getTsfalse();
		case 12:
			return getTmacro();
		case 13:
			return getTfalse();
		case 14:
			return getJump();
		case 15:
			return isMboolean();
		case 16:
			return getMbfalse();
		case 17:
			return getMstring();
		case 18:
			return getMsfalse();
		case 19:
			return getMnumber();
		case 20:
			return getMnfalse();
		case 21:
			return getMfloat();
		case 22:
			return getMffalse();
		case 23:
			return getTtype();
		case 24:
			return getTtfalse();
		case 25:
			return getVwrite();
		case 26:
			return getVwrange();
		case 27:
			return getVstart();
		case 28:
			return getVadd();
		case 29:
			return getVarange();
		case 30:
			return getError();
		case 31:
			return getErange();
		case 32:
			return getNotstarted();
		case 33:
			return getNotfinished();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateMatchStep(int field, @SuppressWarnings("unused") Object... key) {
		switch (field) {
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
			return FieldType.STRING;
		case 6:
			return FieldType.INTEGER;
		case 7:
			return FieldType.INTEGER;
		case 8:
			return FieldType.INTEGER;
		case 9:
			return FieldType.OBJECT;
		case 10:
			return FieldType.INTEGER;
		case 11:
			return FieldType.INTEGER;
		case 12:
			return FieldType.OBJECT;
		case 13:
			return FieldType.INTEGER;
		case 14:
			return FieldType.INTEGER;
		case 15:
			return FieldType.BOOLEAN;
		case 16:
			return FieldType.INTEGER;
		case 17:
			return FieldType.STRING;
		case 18:
			return FieldType.INTEGER;
		case 19:
			return FieldType.LONG;
		case 20:
			return FieldType.INTEGER;
		case 21:
			return FieldType.FLOAT;
		case 22:
			return FieldType.INTEGER;
		case 23:
			return FieldType.STRING;
		case 24:
			return FieldType.INTEGER;
		case 25:
			return FieldType.OBJECT;
		case 26:
			return FieldType.INTEGER;
		case 27:
			return FieldType.OBJECT;
		case 28:
			return FieldType.OBJECT;
		case 29:
			return FieldType.INTEGER;
		case 30:
			return FieldType.STRING;
		case 31:
			return FieldType.INTEGER;
		case 32:
			return FieldType.INTEGER;
		case 33:
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
			return "parm";
		case 4:
			return "pfalse";
		case 5:
			return "field";
		case 6:
			return "ffalse";
		case 7:
			return "altnr";
		case 8:
			return "afalse";
		case 9:
			return "avar";
		case 10:
			return "tstack";
		case 11:
			return "tsfalse";
		case 12:
			return "tmacro";
		case 13:
			return "tfalse";
		case 14:
			return "jump";
		case 15:
			return "mboolean";
		case 16:
			return "mbfalse";
		case 17:
			return "mstring";
		case 18:
			return "msfalse";
		case 19:
			return "mnumber";
		case 20:
			return "mnfalse";
		case 21:
			return "mfloat";
		case 22:
			return "mffalse";
		case 23:
			return "ttype";
		case 24:
			return "ttfalse";
		case 25:
			return "vwrite";
		case 26:
			return "vwrange";
		case 27:
			return "vstart";
		case 28:
			return "vadd";
		case 29:
			return "varange";
		case 30:
			return "error";
		case 31:
			return "erange";
		case 32:
			return "notstarted";
		case 33:
			return "notfinished";
		default:
			return null;
		}
	}
}
