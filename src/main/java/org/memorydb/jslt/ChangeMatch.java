package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface Match
 */
public interface ChangeMatch extends ChangeInterface, Match {
	public default void defaultMatch() {
		setType(null);
	}

	default void setType(Type value) {
		if (getRec() != 0) {
			if (value == null) {
				getStore().setByte(getRec(), matchPosition() + 0, (getStore().getByte(getRec(), matchPosition() + 0) & 224) + 0);
				return;
			}
			getStore().setByte(getRec(), matchPosition() + 0, (getStore().getByte(getRec(), matchPosition() + 0) & 224) + 1 + value.ordinal());
			switch (value) {
			case ARRAY:
				getStore().setInt(getRec(), matchPosition() + 1, 0);
				break;
			case VARIABLE:
				getStore().setInt(getRec(), matchPosition() + 1, 0);
				break;
			case BOOLEAN:
				getStore().setByte(getRec(), matchPosition() + 1, getStore().getByte(getRec(), matchPosition() + 1) & 254);
				break;
			case FLOAT:
				getStore().setLong(getRec(), matchPosition() + 1, Double.doubleToLongBits(0.0));
				break;
			case NUMBER:
				getStore().setLong(getRec(), matchPosition() + 1, 0L);
				break;
			case STRING:
				getStore().setInt(getRec(), matchPosition() + 1, 0);
				break;
			case ITERATE:
				getStore().setInt(getRec(), matchPosition() + 1, 0);
				break;
			case OBJECT:
				getStore().setInt(getRec(), matchPosition() + 1, 0);
				break;
			default:
				break;
			}
		}
	}

	default void moveMarray(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 1, getStore().getInt(other.getRec(), other.matchPosition() + 1));
		getStore().setInt(other.getRec(), other.matchPosition() + 1, 0);
	}

	default void setVariable(Variable value) {
		if (getType() == Type.VARIABLE) {
			getStore().setInt(getRec(), matchPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setBoolean(boolean value) {
		if (getType() == Type.BOOLEAN) {
			getStore().setByte(getRec(), matchPosition() + 1, (getStore().getByte(getRec(), matchPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setFloat(double value) {
		if (getType() == Type.FLOAT) {
			getStore().setLong(getRec(), matchPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setNumber(long value) {
		if (getType() == Type.NUMBER) {
			getStore().setLong(getRec(), matchPosition() + 1, value);
		}
	}

	default void setString(String value) {
		if (getType() == Type.STRING) {
			getStore().setInt(getRec(), matchPosition() + 1, getStore().putString(value));
		}
	}

	default void moveIterate(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 1, getStore().getInt(other.getRec(), other.matchPosition() + 1));
		getStore().setInt(other.getRec(), other.matchPosition() + 1, 0);
	}

	default void moveMobject(ChangeMatch other) {
		getStore().setInt(getRec(), matchPosition() + 1, getStore().getInt(other.getRec(), other.matchPosition() + 1));
		getStore().setInt(other.getRec(), other.matchPosition() + 1, 0);
	}

	default void parseMatch(Parser parser) {
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
		if (parser.hasSub("variable")) {
			setVariable(new Variable(getStore()).parse(parser));
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
		if (parser.hasSub("iterate")) {
			try (IterateArray sub = new IterateArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("mobject")) {
			try (MobjectArray sub = new MobjectArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setMatch(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Type)
				setType((Type) value);
			return value instanceof Type;
		case 3:
			if (value instanceof Variable)
				setVariable((Variable) value);
			return value instanceof Variable;
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
		default:
			return false;
		}
	}

	default ChangeInterface addMatch(int field) {
		switch (field) {
		case 2:
			return addMarray();
		case 8:
			return addIterate();
		case 9:
			return addMobject();
		default:
			return null;
		}
	}
}