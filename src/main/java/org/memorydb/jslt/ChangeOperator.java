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
		if (rec() != 0) {
			if (value == null) {
				store().setByte(rec(), operatorPosition() + 0, (store().getByte(rec(), operatorPosition() + 0) & 192) + 0);
				return;
			}
			store().setByte(rec(), operatorPosition() + 0, (store().getByte(rec(), operatorPosition() + 0) & 192) + 1 + value.ordinal());
			switch (value) {
			case FUNCTION:
				store().setByte(rec(), operatorPosition() + 1, (store().getByte(rec(), operatorPosition() + 1) & 128) + 1 + Function.NEG.ordinal());
				store().setInt(rec(), operatorPosition() + 2, 0);
				store().setInt(rec(), operatorPosition() + 6, 0);
				break;
			case CONDITION:
				store().setInt(rec(), operatorPosition() + 1, 0);
				store().setInt(rec(), operatorPosition() + 5, 0);
				store().setInt(rec(), operatorPosition() + 9, 0);
				break;
			case NUMBER:
				store().setLong(rec(), operatorPosition() + 1, Long.MIN_VALUE);
				break;
			case FLOAT:
				store().setLong(rec(), operatorPosition() + 1, Double.doubleToLongBits(Double.NaN));
				break;
			case STRING:
				store().setInt(rec(), operatorPosition() + 1, Integer.MIN_VALUE);
				break;
			case ARRAY:
				store().setInt(rec(), operatorPosition() + 1, 0);
				break;
			case APPEND:
				store().setInt(rec(), operatorPosition() + 1, 0);
				break;
			case OBJECT:
				store().setInt(rec(), operatorPosition() + 1, 0);
				break;
			case BOOLEAN:
				store().setByte(rec(), operatorPosition() + 1, store().getByte(rec(), operatorPosition() + 1) & 254);
				break;
			case CALL:
				store().setInt(rec(), operatorPosition() + 1, 0);
				store().setInt(rec(), operatorPosition() + 5, 0);
				break;
			case FILTER:
				store().setInt(rec(), operatorPosition() + 1, 0);
				store().setByte(rec(), operatorPosition() + 5, store().getByte(rec(), operatorPosition() + 5) & 254);
				store().setInt(rec(), operatorPosition() + 6, 0);
				break;
			case SORT:
				store().setInt(rec(), operatorPosition() + 1, 0);
				store().setInt(rec(), operatorPosition() + 5, 0);
				break;
			case IF:
				store().setInt(rec(), operatorPosition() + 1, 0);
				store().setInt(rec(), operatorPosition() + 5, 0);
				store().setInt(rec(), operatorPosition() + 9, 0);
				break;
			case READ:
				store().setInt(rec(), operatorPosition() + 1, Integer.MIN_VALUE);
				store().setInt(rec(), operatorPosition() + 5, Integer.MIN_VALUE);
				break;
			case VARIABLE:
				store().setInt(rec(), operatorPosition() + 1, Integer.MIN_VALUE);
				store().setInt(rec(), operatorPosition() + 5, Integer.MIN_VALUE);
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
			store().setByte(rec(), operatorPosition() + 1, (store().getByte(rec(), operatorPosition() + 1) & 128) + 1 + value.ordinal());
		}
	}

	default void setFnParm1(Expr value) {
		if (getOperation() == Operation.FUNCTION) {
			store().setInt(rec(), operatorPosition() + 2, value == null ? 0 : value.rec());
		}
	}

	default void setFnParm2(Expr value) {
		if (getOperation() == Operation.FUNCTION) {
			store().setInt(rec(), operatorPosition() + 6, value == null ? 0 : value.rec());
		}
	}

	default void setConExpr(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			store().setInt(rec(), operatorPosition() + 1, value == null ? 0 : value.rec());
		}
	}

	default void setConTrue(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			store().setInt(rec(), operatorPosition() + 5, value == null ? 0 : value.rec());
		}
	}

	default void setConFalse(Expr value) {
		if (getOperation() == Operation.CONDITION) {
			store().setInt(rec(), operatorPosition() + 9, value == null ? 0 : value.rec());
		}
	}

	default void setNumber(long value) {
		if (getOperation() == Operation.NUMBER) {
			store().setLong(rec(), operatorPosition() + 1, value);
		}
	}

	default void setFloat(double value) {
		if (getOperation() == Operation.FLOAT) {
			store().setLong(rec(), operatorPosition() + 1, Double.doubleToLongBits(value));
		}
	}

	default void setString(String value) {
		if (getOperation() == Operation.STRING) {
			store().setInt(rec(), operatorPosition() + 1, store().putString(value));
		}
	}

	default void moveArray(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 1, store().getInt(other.rec(), other.operatorPosition() + 1));
		store().setInt(other.rec(), other.operatorPosition() + 1, 0);
	}

	default void moveAppend(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 1, store().getInt(other.rec(), other.operatorPosition() + 1));
		store().setInt(other.rec(), other.operatorPosition() + 1, 0);
	}

	default void moveObject(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 1, store().getInt(other.rec(), other.operatorPosition() + 1));
		store().setInt(other.rec(), other.operatorPosition() + 1, 0);
	}

	default void setBoolean(boolean value) {
		if (getOperation() == Operation.BOOLEAN) {
			store().setByte(rec(), operatorPosition() + 1, (store().getByte(rec(), operatorPosition() + 1) & 254) + (value ? 1 : 0));
		}
	}

	default void setMacro(Macro value) {
		if (getOperation() == Operation.CALL) {
			store().setInt(rec(), operatorPosition() + 1, value == null ? 0 : value.rec());
		}
	}

	default void moveCallParms(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 5, store().getInt(other.rec(), other.operatorPosition() + 5));
		store().setInt(other.rec(), other.operatorPosition() + 5, 0);
	}

	default void setFilter(Expr value) {
		if (getOperation() == Operation.FILTER) {
			store().setInt(rec(), operatorPosition() + 1, value == null ? 0 : value.rec());
		}
	}

	default void setFilterDeep(boolean value) {
		if (getOperation() == Operation.FILTER) {
			store().setByte(rec(), operatorPosition() + 5, (store().getByte(rec(), operatorPosition() + 5) & 254) + (value ? 1 : 0));
		}
	}

	default void setFilterExpr(Expr value) {
		if (getOperation() == Operation.FILTER) {
			store().setInt(rec(), operatorPosition() + 6, value == null ? 0 : value.rec());
		}
	}

	default void setSort(Expr value) {
		if (getOperation() == Operation.SORT) {
			store().setInt(rec(), operatorPosition() + 1, value == null ? 0 : value.rec());
		}
	}

	default void moveSortParms(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 5, store().getInt(other.rec(), other.operatorPosition() + 5));
		store().setInt(other.rec(), other.operatorPosition() + 5, 0);
	}

	default void setIf(Expr value) {
		if (getOperation() == Operation.IF) {
			store().setInt(rec(), operatorPosition() + 1, value == null ? 0 : value.rec());
		}
	}

	default void moveIfTrue(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 5, store().getInt(other.rec(), other.operatorPosition() + 5));
		store().setInt(other.rec(), other.operatorPosition() + 5, 0);
	}

	default void moveIfFalse(ChangeOperator other) {
		store().setInt(rec(), operatorPosition() + 9, store().getInt(other.rec(), other.operatorPosition() + 9));
		store().setInt(other.rec(), other.operatorPosition() + 9, 0);
	}

	default void setListenSource(String value) {
		if (getOperation() == Operation.READ) {
			store().setInt(rec(), operatorPosition() + 1, store().putString(value));
		}
	}

	default void setListemNr(int value) {
		if (getOperation() == Operation.READ) {
			store().setInt(rec(), operatorPosition() + 5, value);
		}
	}

	default void setVarName(String value) {
		if (getOperation() == Operation.VARIABLE) {
			store().setInt(rec(), operatorPosition() + 1, store().putString(value));
		}
	}

	default void setVarNr(int value) {
		if (getOperation() == Operation.VARIABLE) {
			store().setInt(rec(), operatorPosition() + 5, value);
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
			setFnParm1(Expr.parse(parser, store()));
		}
		if (parser.hasSub("fnParm2")) {
			setFnParm2(Expr.parse(parser, store()));
		}
		if (parser.hasSub("conExpr")) {
			setConExpr(Expr.parse(parser, store()));
		}
		if (parser.hasSub("conTrue")) {
			setConTrue(Expr.parse(parser, store()));
		}
		if (parser.hasSub("conFalse")) {
			setConFalse(Expr.parse(parser, store()));
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
			ArrayArray sub = new ArrayArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("append")) {
			AppendArray sub = new AppendArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("object")) {
			ObjectArray sub = new ObjectArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasField("boolean")) {
			Boolean valueBoolean = parser.getBoolean("boolean");
			if (valueBoolean == null)
				throw new MutationException("Mandatory 'boolean' field");
			setBoolean(valueBoolean);
		}
		if (parser.hasField("macro")) {
			parser.getRelation("macro", (recNr, idx) -> {
				Macro relRec = Macro.parseKey(parser, store());
				ChangeOperator old = (ChangeOperator) this.copy(recNr);
				old.setMacro(relRec);
				return relRec != null;
			}, rec());
		}
		if (parser.hasSub("callParms")) {
			CallParmsArray sub = new CallParmsArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("filter")) {
			setFilter(Expr.parse(parser, store()));
		}
		if (parser.hasField("filterDeep")) {
			Boolean valueFilterDeep = parser.getBoolean("filterDeep");
			if (valueFilterDeep == null)
				throw new MutationException("Mandatory 'filterDeep' field");
			setFilterDeep(valueFilterDeep);
		}
		if (parser.hasSub("filterExpr")) {
			setFilterExpr(Expr.parse(parser, store()));
		}
		if (parser.hasSub("sort")) {
			setSort(Expr.parse(parser, store()));
		}
		if (parser.hasSub("sortParms")) {
			SortParmsArray sub = new SortParmsArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("if")) {
			setIf(Expr.parse(parser, store()));
		}
		if (parser.hasSub("ifTrue")) {
			IfTrueArray sub = new IfTrueArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasSub("ifFalse")) {
			IfFalseArray sub = new IfFalseArray(this, -1);
			while (parser.getSub())
				sub.add().parse(parser);
		}
		if (parser.hasField("listenSource")) {
			setListenSource(parser.getString("listenSource"));
		}
		if (parser.hasField("listemNr")) {
			setListemNr(parser.getInt("listemNr"));
		}
		if (parser.hasField("varName")) {
			setVarName(parser.getString("varName"));
		}
		if (parser.hasField("varNr")) {
			setVarNr(parser.getInt("varNr"));
		}
	}

	default boolean setOperator(int field, Object value) {
		if (field > 28 && field <= 30)
			return setResultType(field - 28, value);
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
				setFilter((Expr) value);
			return value instanceof Expr;
		case 18:
			if (value instanceof Boolean)
				setFilterDeep((Boolean) value);
			return value instanceof Boolean;
		case 19:
			if (value instanceof Expr)
				setFilterExpr((Expr) value);
			return value instanceof Expr;
		case 20:
			if (value instanceof Expr)
				setSort((Expr) value);
			return value instanceof Expr;
		case 22:
			if (value instanceof Expr)
				setIf((Expr) value);
			return value instanceof Expr;
		case 25:
			if (value instanceof String)
				setListenSource((String) value);
			return value instanceof String;
		case 26:
			if (value instanceof Integer)
				setListemNr((Integer) value);
			return value instanceof Integer;
		case 27:
			if (value instanceof String)
				setVarName((String) value);
			return value instanceof String;
		case 28:
			if (value instanceof Integer)
				setVarNr((Integer) value);
			return value instanceof Integer;
		default:
			return false;
		}
	}

	default ChangeInterface addOperator(int field) {
		if (field > 28 && field <= 30)
			return addResultType(field - 28);
		switch (field) {
		case 11:
			return addArray();
		case 12:
			return addAppend();
		case 13:
			return addObject();
		case 16:
			return addCallParms();
		case 21:
			return addSortParms();
		case 23:
			return addIfTrue();
		case 24:
			return addIfFalse();
		default:
			return null;
		}
	}
}