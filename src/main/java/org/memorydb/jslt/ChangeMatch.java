package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface Match
 */
public interface ChangeMatch extends ChangeInterface, Match {
	public default void defaultMatch() {
		setVariable(null);
		setType(null);
	}

	default void setVariable(Variable value) {
		if (getRec() != 0) {
			getStore().setInt(getRec(), matchPosition() + 0, value == null ? 0 : value.getRec());
		}
	}

	default void setType(Type value) {
		if (getRec() != 0) {
			if (value == null) {
				getStore().setByte(getRec(), matchPosition() + 4, (getStore().getByte(getRec(), matchPosition() + 4) & 224) + 0);
				return;
			}
			getStore().setByte(getRec(), matchPosition() + 4, (getStore().getByte(getRec(), matchPosition() + 4) & 224) + 1 + value.ordinal());
			switch (value) {
			case ARRAY:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				break;
			case BOOLEAN:
				getStore().setByte(getRec(), matchPosition() + 5, getStore().getByte(getRec(), matchPosition() + 5) & 254);
				break;
			case FLOAT:
				getStore().setLong(getRec(), matchPosition() + 5, Double.doubleToLongBits(0.0));
				break;
			case NUMBER:
				getStore().setLong(getRec(), matchPosition() + 5, 0L);
				break;
			case STRING:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				break;
			case OBJECT:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				break;
			case CONSTANT:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				getStore().setInt(getRec(), matchPosition() + 9, 0);
				break;
			case MACRO:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				getStore().setInt(getRec(), matchPosition() + 9, 0);
				break;
			case MULTIPLE:
				getStore().setInt(getRec(), matchPosition() + 5, 0);
				getStore().setByte(getRec(), matchPosition() + 9, (byte) 0);
				getStore().setByte(getRec(), matchPosition() + 10, (byte) 0);
				break;
			default:
				break;
			}
		}
	}

	default void moveMarray(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 5, getStore().getInt(other.getRec(), other.matchPosition() + 5));
		getStore().setInt(other.getRec(), other.matchPosition() + 5, 0);
	}

	default void setBoolean(boolean value) {
		if (getType() == Type.BOOLEAN) {
			getStore().setByte(getRec(), matchPosition() + 5, (getStore().getByte(getRec(), matchPosition() + 5) & 254) + (value ? 1 : 0));
		}
	}

	default void setFloat(double value) {
		if (getType() == Type.FLOAT) {
			getStore().setLong(getRec(), matchPosition() + 5, Double.doubleToLongBits(value));
		}
	}

	default void setNumber(long value) {
		if (getType() == Type.NUMBER) {
			getStore().setLong(getRec(), matchPosition() + 5, value);
		}
	}

	default void setString(String value) {
		if (getType() == Type.STRING) {
			getStore().setInt(getRec(), matchPosition() + 5, getStore().putString(value));
		}
	}

	default void moveMobject(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 5, getStore().getInt(other.getRec(), other.matchPosition() + 5));
		getStore().setInt(other.getRec(), other.matchPosition() + 5, 0);
	}

	default void setCparm(String value) {
		if (getType() == Type.CONSTANT) {
			getStore().setInt(getRec(), matchPosition() + 5, getStore().putString(value));
		}
	}

	default void setConstant(int value) {
		if (getType() == Type.CONSTANT) {
			getStore().setInt(getRec(), matchPosition() + 9, value);
		}
	}

	default void setMacro(Macro value) {
		if (getType() == Type.MACRO) {
			getStore().setInt(getRec(), matchPosition() + 5, value == null ? 0 : value.getRec());
		}
	}

	default void moveMparms(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 9, getStore().getInt(other.getRec(), other.matchPosition() + 9));
		getStore().setInt(other.getRec(), other.matchPosition() + 9, 0);
	}

	default void setMmatch(MatchObject value) {
		if (getType() == Type.MULTIPLE) {
			getStore().setInt(getRec(), matchPosition() + 5, value == null ? 0 : value.getRec());
		}
	}

	default void setMmin(byte value) {
		if (getType() == Type.MULTIPLE) {
			getStore().setByte(getRec(), matchPosition() + 9, value);
		}
	}

	default void setMmax(byte value) {
		if (getType() == Type.MULTIPLE) {
			getStore().setByte(getRec(), matchPosition() + 10, value);
		}
	}

	default void parseMatch(Parser parser) {
		if (parser.hasSub("variable")) {
			setVariable(new Variable(getStore()).parse(parser));
		}
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field Match.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasSub("marray")) {
			try (MarrayArray sub = new MarrayArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("boolean")) {
			Boolean valueBoolean = parser.getBoolean("boolean");
			if (valueBoolean == null)
				throw new MutationException("Mandatory 'boolean' field");
			setBoolean(valueBoolean);
		}
		if (parser.hasField("float")) {
			setFloat(parser.getDouble("float"));
		}
		if (parser.hasField("number")) {
			setNumber(parser.getLong("number"));
		}
		if (parser.hasField("string")) {
			setString(parser.getString("string"));
		}
		if (parser.hasSub("mobject")) {
			try (MobjectArray sub = new MobjectArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("cparm")) {
			setCparm(parser.getString("cparm"));
		}
		if (parser.hasField("constant")) {
			setConstant(parser.getInt("constant"));
		}
		if (parser.hasField("macro")) {
			parser.getRelation("macro", (recNr, idx) -> {
				Macro relRec = new Macro(getStore());
				boolean found = relRec.parseKey(parser);
				setMacro(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasSub("mparms")) {
			try (MparmsArray sub = new MparmsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("mmatch")) {
			setMmatch(new MatchObject(getStore()).parse(parser));
		}
		if (parser.hasField("mmin")) {
			setMmin((byte) parser.getInt("mmin"));
		}
		if (parser.hasField("mmax")) {
			setMmax((byte) parser.getInt("mmax"));
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setMatch(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Variable)
				setVariable((Variable) value);
			return value instanceof Variable;
		case 2:
			if (value instanceof Type)
				setType((Type) value);
			return value instanceof Type;
		case 4:
			if (value instanceof Boolean)
				setBoolean((Boolean) value);
			return value instanceof Boolean;
		case 5:
			if (value instanceof Double)
				setFloat((Double) value);
			return value instanceof Double;
		case 6:
			if (value instanceof Long)
				setNumber((Long) value);
			return value instanceof Long;
		case 7:
			if (value instanceof String)
				setString((String) value);
			return value instanceof String;
		case 9:
			if (value instanceof String)
				setCparm((String) value);
			return value instanceof String;
		case 10:
			if (value instanceof Integer)
				setConstant((Integer) value);
			return value instanceof Integer;
		case 11:
			if (value instanceof Macro)
				setMacro((Macro) value);
			return value instanceof Macro;
		case 13:
			if (value instanceof MatchObject)
				setMmatch((MatchObject) value);
			return value instanceof MatchObject;
		case 14:
			if (value instanceof Byte)
				setMmin((Byte) value);
			return value instanceof Byte;
		case 15:
			if (value instanceof Byte)
				setMmax((Byte) value);
			return value instanceof Byte;
		default:
			return false;
		}
	}

	default ChangeInterface addMatch(int field) {
		switch (field) {
		case 3:
			return addMarray();
		case 8:
			return addMobject();
		case 12:
			return addMparms();
		default:
			return null;
		}
	}
}