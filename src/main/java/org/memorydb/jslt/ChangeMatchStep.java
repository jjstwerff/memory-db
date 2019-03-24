package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface MatchStep
 */
public interface ChangeMatchStep extends ChangeInterface, MatchStep {
	public default void defaultMatchStep() {
		setType(null);
	}

	default void setType(Type value) {
		if (getRec() != 0) {
			if (value == null) {
				getStore().setByte(getRec(), matchstepPosition() + 0, (getStore().getByte(getRec(), matchstepPosition() + 0) & 192) + 0);
				return;
			}
			getStore().setByte(getRec(), matchstepPosition() + 0, (getStore().getByte(getRec(), matchstepPosition() + 0) & 192) + 1 + value.ordinal());
			switch (value) {
			case STACK:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				break;
			case PARM:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case FIELD:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case ALT:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				getStore().setInt(getRec(), matchstepPosition() + 9, 0);
				break;
			case TEST_STACK:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case TEST_CALL:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case JUMP:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				break;
			case TEST_BOOLEAN:
				getStore().setByte(getRec(), matchstepPosition() + 1, getStore().getByte(getRec(), matchstepPosition() + 1) & 254);
				getStore().setInt(getRec(), matchstepPosition() + 2, 0);
				break;
			case TEST_STRING:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case TEST_NUMBER:
				getStore().setLong(getRec(), matchstepPosition() + 1, 0L);
				getStore().setInt(getRec(), matchstepPosition() + 9, 0);
				break;
			case TEST_FLOAT:
				getStore().setLong(getRec(), matchstepPosition() + 1, Double.doubleToLongBits(0.0));
				getStore().setInt(getRec(), matchstepPosition() + 9, 0);
				break;
			case TEST_TYPE:
				getStore().setByte(getRec(), matchstepPosition() + 1, (getStore().getByte(getRec(), matchstepPosition() + 1) & 224) + 0);
				getStore().setInt(getRec(), matchstepPosition() + 2, 0);
				break;
			case VAR_WRITE:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case VAR_START:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				break;
			case VAR_ADD:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case ERROR:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				getStore().setInt(getRec(), matchstepPosition() + 5, 0);
				break;
			case START:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				break;
			case FINISH:
				getStore().setInt(getRec(), matchstepPosition() + 1, 0);
				break;
			default:
				break;
			}
		}
	}

	default void setStack(int value) {
		if (getType() == Type.STACK) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setParm(int value) {
		if (getType() == Type.PARM) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setPfalse(int value) {
		if (getType() == Type.PARM) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setField(String value) {
		if (getType() == Type.FIELD) {
			getStore().setInt(getRec(), matchstepPosition() + 1, getStore().putString(value));
		}
	}

	default void setFfalse(int value) {
		if (getType() == Type.FIELD) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setAltnr(int value) {
		if (getType() == Type.ALT) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setAfalse(int value) {
		if (getType() == Type.ALT) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setAvar(Variable value) {
		if (getType() == Type.ALT) {
			getStore().setInt(getRec(), matchstepPosition() + 9, value == null ? 0 : value.getRec());
		}
	}

	default void setTstack(int value) {
		if (getType() == Type.TEST_STACK) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setTsfalse(int value) {
		if (getType() == Type.TEST_STACK) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setTmacro(Macro value) {
		if (getType() == Type.TEST_CALL) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setTfalse(int value) {
		if (getType() == Type.TEST_CALL) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setJump(int value) {
		if (getType() == Type.JUMP) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setMboolean(boolean value) {
		if (getType() == Type.TEST_BOOLEAN) {
			getStore().setByte(getRec(), matchstepPosition() + 1, (getStore().getByte(getRec(), matchstepPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setMbfalse(int value) {
		if (getType() == Type.TEST_BOOLEAN) {
			getStore().setInt(getRec(), matchstepPosition() + 2, value);
		}
	}

	default void setMstring(String value) {
		if (getType() == Type.TEST_STRING) {
			getStore().setInt(getRec(), matchstepPosition() + 1, getStore().putString(value));
		}
	}

	default void setMsfalse(int value) {
		if (getType() == Type.TEST_STRING) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setMnumber(long value) {
		if (getType() == Type.TEST_NUMBER) {
			getStore().setLong(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setMnfalse(int value) {
		if (getType() == Type.TEST_NUMBER) {
			getStore().setInt(getRec(), matchstepPosition() + 9, value);
		}
	}

	default void setMfloat(double value) {
		if (getType() == Type.TEST_FLOAT) {
			getStore().setLong(getRec(), matchstepPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setMffalse(int value) {
		if (getType() == Type.TEST_FLOAT) {
			getStore().setInt(getRec(), matchstepPosition() + 9, value);
		}
	}

	default void setTtype(Ttype value) {
		if (getType() == Type.TEST_TYPE) {
			if (value == null) {
				getStore().setByte(getRec(), matchstepPosition() + 1, (getStore().getByte(getRec(), matchstepPosition() + 1) & 224) + 0);
				return;
			}
			getStore().setByte(getRec(), matchstepPosition() + 1, (getStore().getByte(getRec(), matchstepPosition() + 1) & 224) + 1 + value.ordinal());
		}
	}

	default void setTtfalse(int value) {
		if (getType() == Type.TEST_TYPE) {
			getStore().setInt(getRec(), matchstepPosition() + 2, value);
		}
	}

	default void setVwrite(Variable value) {
		if (getType() == Type.VAR_WRITE) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setVwrange(int value) {
		if (getType() == Type.VAR_WRITE) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setVstart(Variable value) {
		if (getType() == Type.VAR_START) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setVadd(Variable value) {
		if (getType() == Type.VAR_ADD) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setVarange(int value) {
		if (getType() == Type.VAR_ADD) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setError(String value) {
		if (getType() == Type.ERROR) {
			getStore().setInt(getRec(), matchstepPosition() + 1, getStore().putString(value));
		}
	}

	default void setErange(int value) {
		if (getType() == Type.ERROR) {
			getStore().setInt(getRec(), matchstepPosition() + 5, value);
		}
	}

	default void setNotstarted(int value) {
		if (getType() == Type.START) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void setNotfinished(int value) {
		if (getType() == Type.FINISH) {
			getStore().setInt(getRec(), matchstepPosition() + 1, value);
		}
	}

	default void parseMatchStep(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field MatchStep.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasField("stack")) {
			setStack(parser.getInt("stack"));
		}
		if (parser.hasField("parm")) {
			setParm(parser.getInt("parm"));
		}
		if (parser.hasField("pfalse")) {
			setPfalse(parser.getInt("pfalse"));
		}
		if (parser.hasField("field")) {
			setField(parser.getString("field"));
		}
		if (parser.hasField("ffalse")) {
			setFfalse(parser.getInt("ffalse"));
		}
		if (parser.hasField("altnr")) {
			setAltnr(parser.getInt("altnr"));
		}
		if (parser.hasField("afalse")) {
			setAfalse(parser.getInt("afalse"));
		}
		if (parser.hasSub("avar")) {
			setAvar(new Variable(getStore()).parse(parser));
		}
		if (parser.hasField("tstack")) {
			setTstack(parser.getInt("tstack"));
		}
		if (parser.hasField("tsfalse")) {
			setTsfalse(parser.getInt("tsfalse"));
		}
		if (parser.hasField("tmacro")) {
			parser.getRelation("tmacro", (recNr, idx) -> {
				Macro relRec = new Macro(getStore());
				boolean found = relRec.parseKey(parser);
				setTmacro(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasField("tfalse")) {
			setTfalse(parser.getInt("tfalse"));
		}
		if (parser.hasField("jump")) {
			setJump(parser.getInt("jump"));
		}
		if (parser.hasField("mboolean")) {
			Boolean valueMboolean = parser.getBoolean("mboolean");
			if (valueMboolean == null)
				throw new MutationException("Mandatory 'mboolean' field");
			setMboolean(valueMboolean);
		}
		if (parser.hasField("mbfalse")) {
			setMbfalse(parser.getInt("mbfalse"));
		}
		if (parser.hasField("mstring")) {
			setMstring(parser.getString("mstring"));
		}
		if (parser.hasField("msfalse")) {
			setMsfalse(parser.getInt("msfalse"));
		}
		if (parser.hasField("mnumber")) {
			setMnumber(parser.getLong("mnumber"));
		}
		if (parser.hasField("mnfalse")) {
			setMnfalse(parser.getInt("mnfalse"));
		}
		if (parser.hasField("mfloat")) {
			setMfloat(parser.getDouble("mfloat"));
		}
		if (parser.hasField("mffalse")) {
			setMffalse(parser.getInt("mffalse"));
		}
		if (parser.hasField("ttype")) {
			String valueTtype = parser.getString("ttype");
			Ttype ttype = Ttype.get(valueTtype);
			if (valueTtype != null && ttype == null)
				parser.error("Cannot parse '" + valueTtype + "' for field MatchStep.ttype");
			setTtype(valueTtype == null ? null : ttype);
		}
		if (parser.hasField("ttfalse")) {
			setTtfalse(parser.getInt("ttfalse"));
		}
		if (parser.hasSub("vwrite")) {
			setVwrite(new Variable(getStore()).parse(parser));
		}
		if (parser.hasField("vwrange")) {
			setVwrange(parser.getInt("vwrange"));
		}
		if (parser.hasSub("vstart")) {
			setVstart(new Variable(getStore()).parse(parser));
		}
		if (parser.hasSub("vadd")) {
			setVadd(new Variable(getStore()).parse(parser));
		}
		if (parser.hasField("varange")) {
			setVarange(parser.getInt("varange"));
		}
		if (parser.hasField("error")) {
			setError(parser.getString("error"));
		}
		if (parser.hasField("erange")) {
			setErange(parser.getInt("erange"));
		}
		if (parser.hasField("notstarted")) {
			setNotstarted(parser.getInt("notstarted"));
		}
		if (parser.hasField("notfinished")) {
			setNotfinished(parser.getInt("notfinished"));
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setMatchStep(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Type)
				setType((Type) value);
			return value instanceof Type;
		case 2:
			if (value instanceof Integer)
				setStack((Integer) value);
			return value instanceof Integer;
		case 3:
			if (value instanceof Integer)
				setParm((Integer) value);
			return value instanceof Integer;
		case 4:
			if (value instanceof Integer)
				setPfalse((Integer) value);
			return value instanceof Integer;
		case 5:
			if (value instanceof String)
				setField((String) value);
			return value instanceof String;
		case 6:
			if (value instanceof Integer)
				setFfalse((Integer) value);
			return value instanceof Integer;
		case 7:
			if (value instanceof Integer)
				setAltnr((Integer) value);
			return value instanceof Integer;
		case 8:
			if (value instanceof Integer)
				setAfalse((Integer) value);
			return value instanceof Integer;
		case 9:
			if (value instanceof Variable)
				setAvar((Variable) value);
			return value instanceof Variable;
		case 10:
			if (value instanceof Integer)
				setTstack((Integer) value);
			return value instanceof Integer;
		case 11:
			if (value instanceof Integer)
				setTsfalse((Integer) value);
			return value instanceof Integer;
		case 12:
			if (value instanceof Macro)
				setTmacro((Macro) value);
			return value instanceof Macro;
		case 13:
			if (value instanceof Integer)
				setTfalse((Integer) value);
			return value instanceof Integer;
		case 14:
			if (value instanceof Integer)
				setJump((Integer) value);
			return value instanceof Integer;
		case 15:
			if (value instanceof Boolean)
				setMboolean((Boolean) value);
			return value instanceof Boolean;
		case 16:
			if (value instanceof Integer)
				setMbfalse((Integer) value);
			return value instanceof Integer;
		case 17:
			if (value instanceof String)
				setMstring((String) value);
			return value instanceof String;
		case 18:
			if (value instanceof Integer)
				setMsfalse((Integer) value);
			return value instanceof Integer;
		case 19:
			if (value instanceof Long)
				setMnumber((Long) value);
			return value instanceof Long;
		case 20:
			if (value instanceof Integer)
				setMnfalse((Integer) value);
			return value instanceof Integer;
		case 21:
			if (value instanceof Double)
				setMfloat((Double) value);
			return value instanceof Double;
		case 22:
			if (value instanceof Integer)
				setMffalse((Integer) value);
			return value instanceof Integer;
		case 23:
			if (value instanceof Ttype)
				setTtype((Ttype) value);
			return value instanceof Ttype;
		case 24:
			if (value instanceof Integer)
				setTtfalse((Integer) value);
			return value instanceof Integer;
		case 25:
			if (value instanceof Variable)
				setVwrite((Variable) value);
			return value instanceof Variable;
		case 26:
			if (value instanceof Integer)
				setVwrange((Integer) value);
			return value instanceof Integer;
		case 27:
			if (value instanceof Variable)
				setVstart((Variable) value);
			return value instanceof Variable;
		case 28:
			if (value instanceof Variable)
				setVadd((Variable) value);
			return value instanceof Variable;
		case 29:
			if (value instanceof Integer)
				setVarange((Integer) value);
			return value instanceof Integer;
		case 30:
			if (value instanceof String)
				setError((String) value);
			return value instanceof String;
		case 31:
			if (value instanceof Integer)
				setErange((Integer) value);
			return value instanceof Integer;
		case 32:
			if (value instanceof Integer)
				setNotstarted((Integer) value);
			return value instanceof Integer;
		case 33:
			if (value instanceof Integer)
				setNotfinished((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	default ChangeInterface addMatchStep(int field) {
		switch (field) {
		default:
			return null;
		}
	}
}