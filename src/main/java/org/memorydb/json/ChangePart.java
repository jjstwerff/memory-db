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
		if (rec() != 0) {
			if (value == null) {
				store().setByte(rec(), partPosition() + 0, (store().getByte(rec(), partPosition() + 0) & 224) + 0);
				return;
			}
			store().setByte(rec(), partPosition() + 0, (store().getByte(rec(), partPosition() + 0) & 224) + 1 + value.ordinal());
			switch (value) {
			case ARRAY:
				store().setInt(rec(), partPosition() + 1, 0);
				break;
			case BOOLEAN:
				store().setByte(rec(), partPosition() + 1, store().getByte(rec(), partPosition() + 1) & 254);
				break;
			case FLOAT:
				store().setLong(rec(), partPosition() + 1, Double.doubleToLongBits(0.0));
				break;
			case NUMBER:
				store().setLong(rec(), partPosition() + 1, 0L);
				break;
			case OBJECT:
				store().setInt(rec(), partPosition() + 1, 0);
				break;
			case STRING:
				store().setInt(rec(), partPosition() + 1, 0);
				break;
			default:
				break;
			}
		}
	}

	default void moveArray(ChangePart other) {
		store().setInt(rec(), partPosition() + 1, store().getInt(other.rec(), other.partPosition() + 1));
		store().setInt(other.rec(), other.partPosition() + 1, 0);
	}

	default void setBoolean(boolean value) {
		if (getType() == Type.BOOLEAN) {
			store().setByte(rec(), partPosition() + 1, (store().getByte(rec(), partPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setFloat(double value) {
		if (getType() == Type.FLOAT) {
			store().setLong(rec(), partPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setNumber(long value) {
		if (getType() == Type.NUMBER) {
			store().setLong(rec(), partPosition() + 1, value);
		}
	}

	default void setValue(String value) {
		if (getType() == Type.STRING) {
			store().setInt(rec(), partPosition() + 1, store().putString(value));
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
			ArrayArray sub = new ArrayArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
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
			Field.parse(parser, this);
		}
		if (parser.hasField("value")) {
			setValue(parser.getString("value"));
		}
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