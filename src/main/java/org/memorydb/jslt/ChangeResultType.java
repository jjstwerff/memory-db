package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface ResultType
 */
public interface ChangeResultType extends ChangeInterface, ResultType {
	public default void defaultResultType() {
		setType(Type.NULL);
		setRecord(null);
	}

	default void setType(Type value) {
		if (value == null)
			throw new MutationException("Mandatory 'type' field");
		if (getRec() != 0) {
			getStore().setByte(getRec(), resulttypePosition() + 0, (getStore().getByte(getRec(), resulttypePosition() + 0) & 224) + 1 + value.ordinal());
		}
	}

	default void setRecord(String value) {
		if (getRec() != 0) {
			getStore().setInt(getRec(), resulttypePosition() + 1, getStore().putString(value));
		}
	}

	default void parseResultType(Parser parser) {
		if (parser.hasField("type")) {
			String valueType = parser.getString("type");
			Type type = Type.get(valueType);
			if (valueType != null && type == null)
				parser.error("Cannot parse '" + valueType + "' for field ResultType.type");
			setType(valueType == null ? null : type);
		}
		if (parser.hasField("record")) {
			setRecord(parser.getString("record"));
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setResultType(int field, Object value) {
		switch (field) {
		case 1:
			if (value instanceof Type)
				setType((Type) value);
			return value instanceof Type;
		case 2:
			if (value instanceof String)
				setRecord((String) value);
			return value instanceof String;
		default:
			return false;
		}
	}

	default ChangeInterface addResultType(int field) {
		switch (field) {
		default:
			return null;
		}
	}
}