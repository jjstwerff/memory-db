package org.memorydb.jslt;

import org.memorydb.file.Parser;
import org.memorydb.handler.MutationException;
import org.memorydb.structure.ChangeInterface;

/**
 * Automatically generated record class for interface Operator
 */
public interface ChangeOperator extends Operator, ChangeResultType {
	public default void defaultOperator() {
		setOperation(null);
	}

	default void setOperation(Operation value) {
		if (getRec() != 0) {
			if (value == null) {
				getStore().setByte(getRec(), operatorPosition() + 0, (getStore().getByte(getRec(), operatorPosition() + 0) & 192) + 0);
				return;
			}
			getStore().setByte(getRec(), operatorPosition() + 0, (getStore().getByte(getRec(), operatorPosition() + 0) & 192) + 1 + value.ordinal());
			switch (value) {
			case FUNCTION:
				getStore().setByte(getRec(), operatorPosition() + 1, (getStore().getByte(getRec(), operatorPosition() + 1) & 192) + 1 + Function.NEG.ordinal());
				getStore().setInt(getRec(), operatorPosition() + 2, 0);
				getStore().setInt(getRec(), operatorPosition() + 6, 0);
				break;
			case CONDITION:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				getStore().setInt(getRec(), operatorPosition() + 9, 0);
				break;
			case NUMBER:
				getStore().setLong(getRec(), operatorPosition() + 1, 0L);
				break;
			case FLOAT:
				getStore().setLong(getRec(), operatorPosition() + 1, Double.doubleToLongBits(0.0));
				break;
			case STRING:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				break;
			case ARRAY:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				break;
			case APPEND:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				break;
			case OBJECT:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				break;
			case BOOLEAN:
				getStore().setByte(getRec(), operatorPosition() + 1, getStore().getByte(getRec(), operatorPosition() + 1) & 254);
				break;
			case CALL:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				break;
			case FOR:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				break;
			case FILTER:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				break;
			case SORT:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				break;
			case IF:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				getStore().setInt(getRec(), operatorPosition() + 9, 0);
				break;
			case READ:
				getStore().setInt(getRec(), operatorPosition() + 1, 0);
				getStore().setInt(getRec(), operatorPosition() + 5, 0);
				break;
			default:
				break;
			}
		}
	}

	default void setFunction(Function value) {
		if (value == null)
			throw new MutationException("Mandatory 'function' field");
		if (getOperation() == Operation.FUNCTION) {
			getStore().setByte(getRec(), operatorPosition() + 1, (getStore().getByte(getRec(), operatorPosition() + 1) & 192) + 1 + value.ordinal());
		}
	}

	default void setFnParm1(Expr value) {
		if (getOperation() == Operation.FUNCTION) {
			getStore().setInt(getRec(), operatorPosition() + 2, value == null ? 0 : value.getRec());
		}
	}

	default void setFnParm2(Expr value) {
		if (getOperation() == Operation.FUNCTION) {
			getStore().setInt(getRec(), operatorPosition() + 6, value == null ? 0 : value.getRec());
		}
	}

	default void setConExpr(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setConTrue(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			getStore().setInt(getRec(), operatorPosition() + 5, value == null ? 0 : value.getRec());
		}
	}

	default void setConFalse(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			getStore().setInt(getRec(), operatorPosition() + 9, value == null ? 0 : value.getRec());
		}
	}

	default void setNumber(long value) {
		if (getOperation() == Operation.NUMBER) {
			getStore().setLong(getRec(), operatorPosition() + 1, value);
		}
	}

	default void setFloat(double value) {
		if (getOperation() == Operation.FLOAT) {
			getStore().setLong(getRec(), operatorPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setString(String value) {
		if (getOperation() == Operation.STRING) {
			getStore().setInt(getRec(), operatorPosition() + 1, getStore().putString(value));
		}
	}

	default void moveArray(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 1, getStore().getInt(other.getRec(), other.operatorPosition() + 1));
		getStore().setInt(other.getRec(), other.operatorPosition() + 1, 0);
	}

	default void moveAppend(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 1, getStore().getInt(other.getRec(), other.operatorPosition() + 1));
		getStore().setInt(other.getRec(), other.operatorPosition() + 1, 0);
	}

	default void moveObject(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 1, getStore().getInt(other.getRec(), other.operatorPosition() + 1));
		getStore().setInt(other.getRec(), other.operatorPosition() + 1, 0);
	}

	default void setBoolean(boolean value) {
		if (getOperation() == Operation.BOOLEAN) {
			getStore().setByte(getRec(), operatorPosition() + 1, (getStore().getByte(getRec(), operatorPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setMacro(Macro value) {
		if (getOperation() == Operation.CALL) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void moveCallParms(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 5, getStore().getInt(other.getRec(), other.operatorPosition() + 5));
		getStore().setInt(other.getRec(), other.operatorPosition() + 5, 0);
	}

	default void setFor(Expr value) {
		if (getOperation() == Operation.FOR) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setForExpr(Expr value) {
		if (getOperation() == Operation.FOR) {
			getStore().setInt(getRec(), operatorPosition() + 5, value == null ? 0 : value.getRec());
		}
	}

	default void setFilter(Expr value) {
		if (getOperation() == Operation.FILTER) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void setFilterExpr(Expr value) {
		if (getOperation() == Operation.FILTER) {
			getStore().setInt(getRec(), operatorPosition() + 5, value == null ? 0 : value.getRec());
		}
	}

	default void setSort(Expr value) {
		if (getOperation() == Operation.SORT) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void moveSortParms(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 5, getStore().getInt(other.getRec(), other.operatorPosition() + 5));
		getStore().setInt(other.getRec(), other.operatorPosition() + 5, 0);
	}

	default void setIf(Expr value) {
		if (getOperation() == Operation.IF) {
			getStore().setInt(getRec(), operatorPosition() + 1, value == null ? 0 : value.getRec());
		}
	}

	default void moveIfTrue(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 5, getStore().getInt(other.getRec(), other.operatorPosition() + 5));
		getStore().setInt(other.getRec(), other.operatorPosition() + 5, 0);
	}

	default void moveIfFalse(ChangeOperator other) {
		getStore().setInt(getRec(), operatorPosition() + 9, getStore().getInt(other.getRec(), other.operatorPosition() + 9));
		getStore().setInt(other.getRec(), other.operatorPosition() + 9, 0);
	}

	default void setListenSource(String value) {
		if (getOperation() == Operation.READ) {
			getStore().setInt(getRec(), operatorPosition() + 1, getStore().putString(value));
		}
	}

	default void setListemNr(int value) {
		if (getOperation() == Operation.READ) {
			getStore().setInt(getRec(), operatorPosition() + 5, value);
		}
	}

	default void parseOperator(Parser parser) {
		if (parser.hasField("operation")) {
			String valueOperation = parser.getString("operation");
			Operation operation = Operation.get(valueOperation);
			if (valueOperation != null && operation == null)
				parser.error("Cannot parse '" + valueOperation + "' for field Operator.operation");
			setOperation(valueOperation == null ? null : operation);
		}
		if (parser.hasField("function")) {
			String valueFunction = parser.getString("function");
			Function function = Function.get(valueFunction);
			if (valueFunction != null && function == null)
				parser.error("Cannot parse '" + valueFunction + "' for field Operator.function");
			setFunction(valueFunction == null ? null : function);
		}
		if (parser.hasSub("fnParm1")) {
			setFnParm1(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("fnParm2")) {
			setFnParm2(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("conExpr")) {
			setConExpr(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("conTrue")) {
			setConTrue(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("conFalse")) {
			setConFalse(new Expr(getStore()).parse(parser));
		}
		if (parser.hasField("number")) {
			setNumber(parser.getLong("number"));
		}
		if (parser.hasField("float")) {
			setFloat(parser.getDouble("float"));
		}
		if (parser.hasField("string")) {
			setString(parser.getString("string"));
		}
		if (parser.hasSub("array")) {
			try (ArrayArray sub = new ArrayArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("append")) {
			try (AppendArray sub = new AppendArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("object")) {
			try (ObjectArray sub = new ObjectArray(this, -1)) {
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
		if (parser.hasField("macro")) {
			parser.getRelation("macro", (int recNr) -> {
				Macro relRec = new Macro(getStore());
				boolean found = relRec.parseKey(parser);
				setMacro(relRec);
				return found;
			}, getRec());
		}
		if (parser.hasSub("callParms")) {
			try (CallParmsArray sub = new CallParmsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("for")) {
			setFor(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("forExpr")) {
			setForExpr(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("filter")) {
			setFilter(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("filterExpr")) {
			setFilterExpr(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("sort")) {
			setSort(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("sortParms")) {
			try (SortParmsArray sub = new SortParmsArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("if")) {
			setIf(new Expr(getStore()).parse(parser));
		}
		if (parser.hasSub("ifTrue")) {
			try (IfTrueArray sub = new IfTrueArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasSub("ifFalse")) {
			try (IfFalseArray sub = new IfFalseArray(this, -1)) {
				while (parser.getSub()) {
					sub.add();
					sub.parse(parser);
				}
			}
		}
		if (parser.hasField("listenSource")) {
			setListenSource(parser.getString("listenSource"));
		}
		if (parser.hasField("listemNr")) {
			setListemNr(parser.getInt("listemNr"));
		}
	}

	@Override
	default void close() {
		// nothing
	}

	default boolean setOperator(int field, Object value) {
		if (field >= 27 && field <= 29)
			return setResultType(field - 27, value);
		switch (field) {
		case 1:
			if (value instanceof Operation)
				setOperation((Operation) value);
			return value instanceof Operation;
		case 2:
			if (value instanceof Function)
				setFunction((Function) value);
			return value instanceof Function;
		case 3:
			if (value instanceof Expr)
				setFnParm1((Expr) value);
			return value instanceof Expr;
		case 4:
			if (value instanceof Expr)
				setFnParm2((Expr) value);
			return value instanceof Expr;
		case 5:
			if (value instanceof Expr)
				setConExpr((Expr) value);
			return value instanceof Expr;
		case 6:
			if (value instanceof Expr)
				setConTrue((Expr) value);
			return value instanceof Expr;
		case 7:
			if (value instanceof Expr)
				setConFalse((Expr) value);
			return value instanceof Expr;
		case 8:
			if (value instanceof Long)
				setNumber((Long) value);
			return value instanceof Long;
		case 9:
			if (value instanceof Double)
				setFloat((Double) value);
			return value instanceof Double;
		case 10:
			if (value instanceof String)
				setString((String) value);
			return value instanceof String;
		case 14:
			if (value instanceof Boolean)
				setBoolean((Boolean) value);
			return value instanceof Boolean;
		case 15:
			if (value instanceof Macro)
				setMacro((Macro) value);
			return value instanceof Macro;
		case 17:
			if (value instanceof Expr)
				setFor((Expr) value);
			return value instanceof Expr;
		case 18:
			if (value instanceof Expr)
				setForExpr((Expr) value);
			return value instanceof Expr;
		case 19:
			if (value instanceof Expr)
				setFilter((Expr) value);
			return value instanceof Expr;
		case 20:
			if (value instanceof Expr)
				setFilterExpr((Expr) value);
			return value instanceof Expr;
		case 21:
			if (value instanceof Expr)
				setSort((Expr) value);
			return value instanceof Expr;
		case 23:
			if (value instanceof Expr)
				setIf((Expr) value);
			return value instanceof Expr;
		case 26:
			if (value instanceof String)
				setListenSource((String) value);
			return value instanceof String;
		case 27:
			if (value instanceof Integer)
				setListemNr((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	default ChangeInterface addOperator(int field) {
		if (field >= 27 && field <= 29)
			return addResultType(field - 27);
		switch (field) {
		case 11:
			return addArray();
		case 12:
			return addAppend();
		case 13:
			return addObject();
		case 16:
			return addCallParms();
		case 22:
			return addSortParms();
		case 24:
			return addIfTrue();
		case 25:
			return addIfFalse();
		default:
			return null;
		}
	}
}