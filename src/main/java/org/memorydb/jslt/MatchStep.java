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
 * Automatically generated record class for table MatchStep
 */
@RecordData(name = "MatchStep")
public interface MatchStep extends MemoryRecord, RecordInterface {
	int matchstepPosition();

	@Override
	Store getStore();

	boolean parseKey(Parser parser);

	default ChangeMatchStep changeMatchStep() {
		if (this instanceof MatchingArray)
			return (MatchingArray) this;
		return null;
	}

	public enum Type {
		STACK, PARM, FIELD, ALT, TEST_CALL, JUMP, TEST_STACK, TEST_BOOLEAN, TEST_STRING, TEST_NUMBER, TEST_FLOAT, TEST_TYPE, POS_KEEP, POS_FREE, POS_TO, VAR_WRITE, VAR_START, VAR_ADD, ERROR;

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
		enumerate = {"STACK", "PARM", "FIELD", "ALT", "TEST_CALL", "JUMP", "TEST_STACK", "TEST_BOOLEAN", "TEST_STRING", "TEST_NUMBER", "TEST_FLOAT", "TEST_TYPE", "POS_KEEP", "POS_FREE", "POS_TO", "VAR_WRITE", "VAR_START", "VAR_ADD", "ERROR"},
		condition = true,
		mandatory = false
	)
	default Type getType() {
		int data = getRec() == 0 ? 0 : getStore().getByte(getRec(), matchstepPosition() + 0) & 63;
		if (data <= 0)
			return null;
		return Type.values()[data - 1];
	}

	@FieldData(
		name = "stack",
		type = "INTEGER",
		when = "STACK",
		mandatory = false
	)
	default int getStack() {
		return getType() != Type.STACK ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "parm",
		type = "INTEGER",
		when = "PARM",
		mandatory = false
	)
	default int getParm() {
		return getType() != Type.PARM ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "pfalse",
		type = "INTEGER",
		when = "PARM",
		mandatory = false
	)
	default int getPfalse() {
		return getType() != Type.PARM ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "field",
		type = "STRING",
		when = "FIELD",
		mandatory = false
	)
	default String getField() {
		return getType() != Type.FIELD ? null : getStore().getString(getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "ffalse",
		type = "INTEGER",
		when = "FIELD",
		mandatory = false
	)
	default int getFfalse() {
		return getType() != Type.FIELD ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "altnr",
		type = "INTEGER",
		when = "ALT",
		mandatory = false
	)
	default int getAltnr() {
		return getType() != Type.ALT ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "afalse",
		type = "INTEGER",
		when = "ALT",
		mandatory = false
	)
	default int getAfalse() {
		return getType() != Type.ALT ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "avar",
		type = "OBJECT",
		related = Variable.class,
		when = "ALT",
		mandatory = false
	)
	default Variable getAvar() {
		return new Variable(getStore(), getType() != Type.ALT ? 0 : getStore().getInt(getRec(), matchstepPosition() + 9));
	}

	@FieldData(
		name = "tstack",
		type = "INTEGER",
		when = "TEST_STACK",
		mandatory = false
	)
	default int getTstack() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "tsfalse",
		type = "INTEGER",
		when = "TEST_STACK",
		mandatory = false
	)
	default int getTsfalse() {
		return getType() != Type.TEST_STACK ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "tmacro",
		type = "RELATION",
		related = Macro.class,
		when = "TEST_CALL",
		mandatory = false
	)
	default Macro getTmacro() {
		return new Macro(getStore(), getType() != Type.TEST_CALL ? 0 : getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "tfalse",
		type = "INTEGER",
		when = "TEST_CALL",
		mandatory = false
	)
	default int getTfalse() {
		return getType() != Type.TEST_CALL ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "jump",
		type = "INTEGER",
		when = "JUMP",
		mandatory = false
	)
	default int getJump() {
		return getType() != Type.JUMP ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "mboolean",
		type = "BOOLEAN",
		when = "TEST_BOOLEAN",
		mandatory = false
	)
	default boolean isMboolean() {
		return getType() != Type.TEST_BOOLEAN ? false : (getStore().getByte(getRec(), matchstepPosition() + 1) & 1) > 0;
	}

	@FieldData(
		name = "mbfalse",
		type = "INTEGER",
		when = "TEST_BOOLEAN",
		mandatory = false
	)
	default int getMbfalse() {
		return getType() != Type.TEST_BOOLEAN ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 2);
	}

	@FieldData(
		name = "mstring",
		type = "STRING",
		when = "TEST_STRING",
		mandatory = false
	)
	default String getMstring() {
		return getType() != Type.TEST_STRING ? null : getStore().getString(getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "msfalse",
		type = "INTEGER",
		when = "TEST_STRING",
		mandatory = false
	)
	default int getMsfalse() {
		return getType() != Type.TEST_STRING ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "mnumber",
		type = "LONG",
		when = "TEST_NUMBER",
		mandatory = false
	)
	default long getMnumber() {
		return getType() != Type.TEST_NUMBER ? Long.MIN_VALUE : getStore().getLong(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "mnfalse",
		type = "INTEGER",
		when = "TEST_NUMBER",
		mandatory = false
	)
	default int getMnfalse() {
		return getType() != Type.TEST_NUMBER ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 9);
	}

	@FieldData(
		name = "mfloat",
		type = "FLOAT",
		when = "TEST_FLOAT",
		mandatory = false
	)
	default double getMfloat() {
		return getType() != Type.TEST_FLOAT ? Double.NaN : Double.longBitsToDouble(getStore().getLong(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "mffalse",
		type = "INTEGER",
		when = "TEST_FLOAT",
		mandatory = false
	)
	default int getMffalse() {
		return getType() != Type.TEST_FLOAT ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 9);
	}

	public enum Ttype {
		TYPE_NULL, TYPE_BOOLEAN, TYPE_STRING, TYPE_NUMBER, TYPE_FLOAT, TYPE_ARRAY, TYPE_OBJECT;

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

	@FieldData(
		name = "ttype",
		type = "ENUMERATE",
		enumerate = {"TYPE_NULL", "TYPE_BOOLEAN", "TYPE_STRING", "TYPE_NUMBER", "TYPE_FLOAT", "TYPE_ARRAY", "TYPE_OBJECT"},
		when = "TEST_TYPE",
		mandatory = false
	)
	default Ttype getTtype() {
		int data = getType() != Type.TEST_TYPE ? 0 : getStore().getByte(getRec(), matchstepPosition() + 1) & 31;
		if (data <= 0)
			return null;
		return Ttype.values()[data - 1];
	}

	@FieldData(
		name = "ttfalse",
		type = "INTEGER",
		when = "TEST_TYPE",
		mandatory = false
	)
	default int getTtfalse() {
		return getType() != Type.TEST_TYPE ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 2);
	}

	@FieldData(
		name = "pto",
		type = "INTEGER",
		when = "POS_TO",
		mandatory = false
	)
	default int getPto() {
		return getType() != Type.POS_TO ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 1);
	}

	@FieldData(
		name = "vwrite",
		type = "OBJECT",
		related = Variable.class,
		when = "VAR_WRITE",
		mandatory = false
	)
	default Variable getVwrite() {
		return new Variable(getStore(), getType() != Type.VAR_WRITE ? 0 : getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "vwfrom",
		type = "INTEGER",
		when = "VAR_WRITE",
		mandatory = false
	)
	default int getVwfrom() {
		return getType() != Type.VAR_WRITE ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "vwtill",
		type = "INTEGER",
		when = "VAR_WRITE",
		mandatory = false
	)
	default int getVwtill() {
		return getType() != Type.VAR_WRITE ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 9);
	}

	@FieldData(
		name = "vstart",
		type = "OBJECT",
		related = Variable.class,
		when = "VAR_START",
		mandatory = false
	)
	default Variable getVstart() {
		return new Variable(getStore(), getType() != Type.VAR_START ? 0 : getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "vadd",
		type = "OBJECT",
		related = Variable.class,
		when = "VAR_ADD",
		mandatory = false
	)
	default Variable getVadd() {
		return new Variable(getStore(), getType() != Type.VAR_ADD ? 0 : getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "vafrom",
		type = "INTEGER",
		when = "VAR_ADD",
		mandatory = false
	)
	default int getVafrom() {
		return getType() != Type.VAR_ADD ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "vatill",
		type = "INTEGER",
		when = "VAR_ADD",
		mandatory = false
	)
	default int getVatill() {
		return getType() != Type.VAR_ADD ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 9);
	}

	@FieldData(
		name = "error",
		type = "STRING",
		when = "ERROR",
		mandatory = false
	)
	default String getError() {
		return getType() != Type.ERROR ? null : getStore().getString(getStore().getInt(getRec(), matchstepPosition() + 1));
	}

	@FieldData(
		name = "efrom",
		type = "INTEGER",
		when = "ERROR",
		mandatory = false
	)
	default int getEfrom() {
		return getType() != Type.ERROR ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 5);
	}

	@FieldData(
		name = "etill",
		type = "INTEGER",
		when = "ERROR",
		mandatory = false
	)
	default int getEtill() {
		return getType() != Type.ERROR ? Integer.MIN_VALUE : getStore().getInt(getRec(), matchstepPosition() + 9);
	}

	default void outputMatchStep(Write write, int iterate) throws IOException {
		if (getRec() == 0 || iterate <= 0)
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
		if (fldAvar != null && fldAvar.getRec() != 0) {
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
		write.field("pto", getPto());
		Variable fldVwrite = getVwrite();
		if (fldVwrite != null && fldVwrite.getRec() != 0) {
			write.sub("vwrite");
			fldVwrite.output(write, iterate);
			write.endSub();
		}
		write.field("vwfrom", getVwfrom());
		write.field("vwtill", getVwtill());
		Variable fldVstart = getVstart();
		if (fldVstart != null && fldVstart.getRec() != 0) {
			write.sub("vstart");
			fldVstart.output(write, iterate);
			write.endSub();
		}
		Variable fldVadd = getVadd();
		if (fldVadd != null && fldVadd.getRec() != 0) {
			write.sub("vadd");
			fldVadd.output(write, iterate);
			write.endSub();
		}
		write.field("vafrom", getVafrom());
		write.field("vatill", getVatill());
		write.field("error", getError());
		write.field("efrom", getEfrom());
		write.field("etill", getEtill());
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
			return getPto();
		case 26:
			return getVwrite();
		case 27:
			return getVwfrom();
		case 28:
			return getVwtill();
		case 29:
			return getVstart();
		case 30:
			return getVadd();
		case 31:
			return getVafrom();
		case 32:
			return getVatill();
		case 33:
			return getError();
		case 34:
			return getEfrom();
		case 35:
			return getEtill();
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
			return FieldType.INTEGER;
		case 26:
			return FieldType.OBJECT;
		case 27:
			return FieldType.INTEGER;
		case 28:
			return FieldType.INTEGER;
		case 29:
			return FieldType.OBJECT;
		case 30:
			return FieldType.OBJECT;
		case 31:
			return FieldType.INTEGER;
		case 32:
			return FieldType.INTEGER;
		case 33:
			return FieldType.STRING;
		case 34:
			return FieldType.INTEGER;
		case 35:
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
			return "pto";
		case 26:
			return "vwrite";
		case 27:
			return "vwfrom";
		case 28:
			return "vwtill";
		case 29:
			return "vstart";
		case 30:
			return "vadd";
		case 31:
			return "vafrom";
		case 32:
			return "vatill";
		case 33:
			return "error";
		case 34:
			return "efrom";
		case 35:
			return "etill";
		default:
			return null;
		}
	}
}
