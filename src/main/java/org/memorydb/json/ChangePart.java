package org.memorydb.json;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface Part
 */
public interface ChangePart extends ChangeInterface, Part {
	public default void defaultPart() {
		setType(Type.NULL);
	}

	default void setType(Type value) {
		if (getRec() != 0) {
			if (value == null) {
				getStore().setByte(getRec(), partPosition() + 0, (getStore().getByte(getRec(), partPosition() + 0) & 224) + 0);
				return;
			}
			getStore().setByte(getRec(), partPosition() + 0, (getStore().getByte(getRec(), partPosition() + 0) & 224) + 1 + value.ordinal());
			switch (value) {
			case ARRAY:
				getStore().setInt(getRec(), partPosition() + 1, 0);
				break;
			case BOOLEAN:
				getStore().setByte(getRec(), partPosition() + 1, getStore().getByte(getRec(), partPosition() + 1) & 254);
				break;
			case FLOAT:
				getStore().setLong(getRec(), partPosition() + 1, Double.doubleToLongBits(0.0));
				break;
			case NUMBER:
				getStore().setLong(getRec(), partPosition() + 1, 0L);
				break;
			case OBJECT:
				getStore().setInt(getRec(), partPosition() + 1, 0);
				break;
			case STRING:
				getStore().setInt(getRec(), partPosition() + 1, 0);
				break;
			default:
				break;
			}
		}
	}

	default void moveArray(ChangePart other) {
		getStore().setInt(getRec(), partPosition() + 1, getStore().getInt(other.getRec(), other.partPosition() + 1));
		getStore().setInt(other.getRec(), other.partPosition() + 1, 0);
	}

	default void setBoolean(boolean value) {
		if (getType() == Type.BOOLEAN) {
			getStore().setByte(getRec(), partPosition() + 1, (getStore().getByte(getRec(), partPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setFloat(double value) {
		if (getType() == Type.FLOAT) {
			getStore().setLong(getRec(), partPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setNumber(long value) {
		if (getType() == Type.NUMBER) {
			getStore().setLong(getRec(), partPosition() + 1, value);
		}
	}

	default void setValue(String value) {
		if (getType() == Type.STRING) {
			getStore().setInt(getRec(), partPosition() + 1, getStore().putString(value));
		}
	}

	default void parsePart(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field Part.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasSub("array")) {
			try (ArrayArray sub = new ArrayArray(this, -1)) {
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
		if (parser.hasSub("object")) {
			new Field(getStore()).parse(parser, this);
		}
		if (parser.hasField("value")) {
			setValue(parser.getString("value"));
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setPart(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Type)
				setType((Type) value);
			return value instanceof Type;
		case 3:
			if (value instanceof Boolean)
				setBoolean((Boolean) value);
			return value instanceof Boolean;
		case 4:
			if (value instanceof Double)
				setFloat((Double) value);
			return value instanceof Double;
		case 5:
			if (value instanceof Long)
				setNumber((Long) value);
			return value instanceof Long;
		case 7:
			if (value instanceof String)
				setValue((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	default ChangeInterface addPart(int field) {
		switch (field) {
		case 2:
			return addArray();
		case 6:
			return addObject();
		default:
			return null;
		}
	}
}