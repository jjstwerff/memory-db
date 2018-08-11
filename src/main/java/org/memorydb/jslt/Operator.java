package org.memorydb.jslt;

import java.io.IOException;

import org.memorydb.file.Parser;
import org.memorydb.file.Write;
import org.memorydb.structure.FieldData;
import org.memorydb.structure.RecordData;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.Store;
import java.util.HashMap;
import java.util.Map;

/**
 * Automatically generated record class for table Operator
 */
@RecordData(name = "Operator")
public interface Operator extends ResultType {
	int operatorPosition();

	@Override
	Store getStore();

	@Override
	default int resulttypePosition() {
		return operatorPosition() + 13;
	}

	@Override
	boolean parseKey(Parser parser);

	default ChangeOperator changeOperator() {
		if (this instanceof ObjectArray)
			return (ObjectArray) this;
		if (this instanceof OrderArray)
			return (OrderArray) this;
		if (this instanceof SliceArray)
			return (SliceArray) this;
		if (this instanceof ArrayArray)
			return (ArrayArray) this;
		if (this instanceof AppendArray)
			return (AppendArray) this;
		if (this instanceof CallParmsArray)
			return (CallParmsArray) this;
		if (this instanceof SortParmsArray)
			return (SortParmsArray) this;
		if (this instanceof IfTrueArray)
			return (IfTrueArray) this;
		if (this instanceof IfFalseArray)
			return (IfFalseArray) this;
		if (this instanceof CodeArray)
			return (CodeArray) this;
		if (this instanceof Expr)
			return new ChangeExpr((Expr) this);
		if (this instanceof Listener)
			return new ChangeListener((Listener) this);
		if (this instanceof Level)
			return new ChangeLevel((Level) this);
		return null;
	}

	public enum Operation {
		FUNCTION, CONDITION, NUMBER, FLOAT, STRING, ARRAY, OBJECT, BOOLEAN, APPEND, NULL, CALL, FOR, SORT, IF, CURRENT, READ;

		private static Map<String, Operation> map = new HashMap<>();

		static {
			for (Operation tp : Operation.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Operation get(String value) {
			return map.get(value);
		}
	}

	@FieldData(
		name = "operation",
		type = "ENUMERATE",
		enumerate = {"FUNCTION", "CONDITION", "NUMBER", "FLOAT", "STRING", "ARRAY", "OBJECT", "BOOLEAN", "APPEND", "NULL", "CALL", "FOR", "SORT", "IF", "CURRENT", "READ"},
		condition = true,
		mandatory = false
	)
	default Operation getOperation() {
		int data = getRec() == 0 ? 0 : getStore().getByte(getRec(), operatorPosition() + 0) & 63;
		if (data <= 0)
			return null;
		return Operation.values()[data - 1];
	}

	public enum Function {
		NEG, ADD, MIN, MUL, DIV, MOD, POW, EQ, NE, LT, GT, LE, GE, AND, OR, NOT, FIRST, LAST, INDEX, LENGTH, NUMBER, FLOAT, FILTER, STRING, BOOLEAN, NAME, TYPE, ELEMENT;

		private static Map<String, Function> map = new HashMap<>();

		static {
			for (Function tp : Function.values()) {
				map.put(tp.toString(), tp);
			}
		}

		public static Function get(String value) {
			return map.get(value);
		}
	}

	@FieldData(
		name = "function",
		type = "ENUMERATE",
		enumerate = {"NEG", "ADD", "MIN", "MUL", "DIV", "MOD", "POW", "EQ", "NE", "LT", "GT", "LE", "GE", "AND", "OR", "NOT", "FIRST", "LAST", "INDEX", "LENGTH", "NUMBER", "FLOAT", "FILTER", "STRING", "BOOLEAN", "NAME", "TYPE", "ELEMENT"},
		when = "FUNCTION",
		mandatory = true
	)
	default Function getFunction() {
		int data = getOperation() != Operation.FUNCTION ? 0 : getStore().getByte(getRec(), operatorPosition() + 1) & 63;
		if (data <= 0)
			return null;
		return Function.values()[data - 1];
	}

	@FieldData(
		name = "fnParm1",
		type = "OBJECT",
		related = Expr.class,
		when = "FUNCTION",
		mandatory = false
	)
	default Expr getFnParm1() {
		return new Expr(getStore(), getOperation() != Operation.FUNCTION ? 0 : getStore().getInt(getRec(), operatorPosition() + 2));
	}

	@FieldData(
		name = "fnParm2",
		type = "OBJECT",
		related = Expr.class,
		when = "FUNCTION",
		mandatory = false
	)
	default Expr getFnParm2() {
		return new Expr(getStore(), getOperation() != Operation.FUNCTION ? 0 : getStore().getInt(getRec(), operatorPosition() + 6));
	}

	@FieldData(
		name = "conExpr",
		type = "OBJECT",
		related = Expr.class,
		when = "CONDITION",
		mandatory = false
	)
	default Expr getConExpr() {
		return new Expr(getStore(), getOperation() != Operation.CONDITION ? 0 : getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "conTrue",
		type = "OBJECT",
		related = Expr.class,
		when = "CONDITION",
		mandatory = false
	)
	default Expr getConTrue() {
		return new Expr(getStore(), getOperation() != Operation.CONDITION ? 0 : getStore().getInt(getRec(), operatorPosition() + 5));
	}

	@FieldData(
		name = "conFalse",
		type = "OBJECT",
		related = Expr.class,
		when = "CONDITION",
		mandatory = false
	)
	default Expr getConFalse() {
		return new Expr(getStore(), getOperation() != Operation.CONDITION ? 0 : getStore().getInt(getRec(), operatorPosition() + 9));
	}

	@FieldData(
		name = "number",
		type = "LONG",
		when = "NUMBER",
		mandatory = false
	)
	default long getNumber() {
		return getOperation() != Operation.NUMBER ? Long.MIN_VALUE : getStore().getLong(getRec(), operatorPosition() + 1);
	}

	@FieldData(
		name = "float",
		type = "FLOAT",
		when = "FLOAT",
		mandatory = false
	)
	default double getFloat() {
		return getOperation() != Operation.FLOAT ? Double.NaN : Double.longBitsToDouble(getStore().getLong(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "string",
		type = "STRING",
		when = "STRING",
		mandatory = false
	)
	default String getString() {
		return getOperation() != Operation.STRING ? null : getStore().getString(getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "array",
		type = "ARRAY",
		related = ArrayArray.class,
		when = "ARRAY",
		mandatory = false
	)
	default ArrayArray getArray() {
		return getOperation() != Operation.ARRAY ? null : new ArrayArray(this, -1);
	}

	default ArrayArray getArray(int index) {
		return getOperation() != Operation.ARRAY ? new ArrayArray(getStore(), 0, -1) : new ArrayArray(this, index);
	}

	default ArrayArray addArray() {
		return getOperation() != Operation.ARRAY ? new ArrayArray(getStore(), 0, -1) : getArray().add();
	}

	@FieldData(
		name = "append",
		type = "ARRAY",
		related = AppendArray.class,
		when = "APPEND",
		mandatory = false
	)
	default AppendArray getAppend() {
		return getOperation() != Operation.APPEND ? null : new AppendArray(this, -1);
	}

	default AppendArray getAppend(int index) {
		return getOperation() != Operation.APPEND ? new AppendArray(getStore(), 0, -1) : new AppendArray(this, index);
	}

	default AppendArray addAppend() {
		return getOperation() != Operation.APPEND ? new AppendArray(getStore(), 0, -1) : getAppend().add();
	}

	@FieldData(
		name = "object",
		type = "ARRAY",
		related = ObjectArray.class,
		when = "OBJECT",
		mandatory = false
	)
	default ObjectArray getObject() {
		return getOperation() != Operation.OBJECT ? null : new ObjectArray(this, -1);
	}

	default ObjectArray getObject(int index) {
		return getOperation() != Operation.OBJECT ? new ObjectArray(getStore(), 0, -1) : new ObjectArray(this, index);
	}

	default ObjectArray addObject() {
		return getOperation() != Operation.OBJECT ? new ObjectArray(getStore(), 0, -1) : getObject().add();
	}

	@FieldData(
		name = "boolean",
		type = "BOOLEAN",
		when = "BOOLEAN",
		mandatory = false
	)
	default boolean isBoolean() {
		return getOperation() != Operation.BOOLEAN ? false : (getStore().getByte(getRec(), operatorPosition() + 1) & 1) > 0;
	}

	@FieldData(
		name = "macro",
		type = "RELATION",
		related = Macro.class,
		when = "CALL",
		mandatory = false
	)
	default Macro getMacro() {
		return new Macro(getStore(), getOperation() != Operation.CALL ? 0 : getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "callParms",
		type = "ARRAY",
		related = CallParmsArray.class,
		when = "CALL",
		mandatory = false
	)
	default CallParmsArray getCallParms() {
		return getOperation() != Operation.CALL ? null : new CallParmsArray(this, -1);
	}

	default CallParmsArray getCallParms(int index) {
		return getOperation() != Operation.CALL ? new CallParmsArray(getStore(), 0, -1) : new CallParmsArray(this, index);
	}

	default CallParmsArray addCallParms() {
		return getOperation() != Operation.CALL ? new CallParmsArray(getStore(), 0, -1) : getCallParms().add();
	}

	@FieldData(
		name = "for",
		type = "OBJECT",
		related = Expr.class,
		when = "FOR",
		mandatory = false
	)
	default Expr getFor() {
		return new Expr(getStore(), getOperation() != Operation.FOR ? 0 : getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "forExpr",
		type = "OBJECT",
		related = Expr.class,
		when = "FOR",
		mandatory = false
	)
	default Expr getForExpr() {
		return new Expr(getStore(), getOperation() != Operation.FOR ? 0 : getStore().getInt(getRec(), operatorPosition() + 5));
	}

	@FieldData(
		name = "sort",
		type = "OBJECT",
		related = Expr.class,
		when = "SORT",
		mandatory = false
	)
	default Expr getSort() {
		return new Expr(getStore(), getOperation() != Operation.SORT ? 0 : getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "sortParms",
		type = "ARRAY",
		related = SortParmsArray.class,
		when = "SORT",
		mandatory = false
	)
	default SortParmsArray getSortParms() {
		return getOperation() != Operation.SORT ? null : new SortParmsArray(this, -1);
	}

	default SortParmsArray getSortParms(int index) {
		return getOperation() != Operation.SORT ? new SortParmsArray(getStore(), 0, -1) : new SortParmsArray(this, index);
	}

	default SortParmsArray addSortParms() {
		return getOperation() != Operation.SORT ? new SortParmsArray(getStore(), 0, -1) : getSortParms().add();
	}

	@FieldData(
		name = "if",
		type = "OBJECT",
		related = Expr.class,
		when = "IF",
		mandatory = false
	)
	default Expr getIf() {
		return new Expr(getStore(), getOperation() != Operation.IF ? 0 : getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "ifTrue",
		type = "ARRAY",
		related = IfTrueArray.class,
		when = "IF",
		mandatory = false
	)
	default IfTrueArray getIfTrue() {
		return getOperation() != Operation.IF ? null : new IfTrueArray(this, -1);
	}

	default IfTrueArray getIfTrue(int index) {
		return getOperation() != Operation.IF ? new IfTrueArray(getStore(), 0, -1) : new IfTrueArray(this, index);
	}

	default IfTrueArray addIfTrue() {
		return getOperation() != Operation.IF ? new IfTrueArray(getStore(), 0, -1) : getIfTrue().add();
	}

	@FieldData(
		name = "ifFalse",
		type = "ARRAY",
		related = IfFalseArray.class,
		when = "IF",
		mandatory = false
	)
	default IfFalseArray getIfFalse() {
		return getOperation() != Operation.IF ? null : new IfFalseArray(this, -1);
	}

	default IfFalseArray getIfFalse(int index) {
		return getOperation() != Operation.IF ? new IfFalseArray(getStore(), 0, -1) : new IfFalseArray(this, index);
	}

	default IfFalseArray addIfFalse() {
		return getOperation() != Operation.IF ? new IfFalseArray(getStore(), 0, -1) : getIfFalse().add();
	}

	@FieldData(
		name = "listenSource",
		type = "STRING",
		related = Listener.class,
		when = "READ",
		mandatory = false
	)
	default String getListenSource() {
		return getOperation() != Operation.READ ? null : getStore().getString(getStore().getInt(getRec(), operatorPosition() + 1));
	}

	@FieldData(
		name = "listemNr",
		type = "INTEGER",
		related = Listener.class,
		when = "READ",
		mandatory = false
	)
	default int getListemNr() {
		return getOperation() != Operation.READ ? Integer.MIN_VALUE : getStore().getInt(getRec(), operatorPosition() + 5);
	}

	default void outputOperator(Write write, int iterate, boolean first) throws IOException {
		if (getRec() == 0 || iterate <= 0)
			return;
		write.field("operation", getOperation(), first);
		write.field("function", getFunction(), false);
		Expr fldFnParm1 = getFnParm1();
		if (fldFnParm1 != null && fldFnParm1.getRec() != 0) {
			write.sub("fnParm1", false);
			fldFnParm1.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldFnParm2 = getFnParm2();
		if (fldFnParm2 != null && fldFnParm2.getRec() != 0) {
			write.sub("fnParm2", false);
			fldFnParm2.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldConExpr = getConExpr();
		if (fldConExpr != null && fldConExpr.getRec() != 0) {
			write.sub("conExpr", false);
			fldConExpr.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldConTrue = getConTrue();
		if (fldConTrue != null && fldConTrue.getRec() != 0) {
			write.sub("conTrue", false);
			fldConTrue.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldConFalse = getConFalse();
		if (fldConFalse != null && fldConFalse.getRec() != 0) {
			write.sub("conFalse", false);
			fldConFalse.output(write, iterate - 1);
			write.endSub();
		}
		write.field("number", getNumber(), false);
		write.field("float", getFloat(), false);
		write.field("string", getString(), false);
		ArrayArray fldArray = getArray();
		if (fldArray != null) {
			write.sub("array", false);
			for (ArrayArray sub : fldArray)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		AppendArray fldAppend = getAppend();
		if (fldAppend != null) {
			write.sub("append", false);
			for (AppendArray sub : fldAppend)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		ObjectArray fldObject = getObject();
		if (fldObject != null) {
			write.sub("object", false);
			for (ObjectArray sub : fldObject)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		if (getOperation() == Operation.BOOLEAN)
			write.field("boolean", isBoolean(), false);
		write.field("macro", getMacro(), false);
		CallParmsArray fldCallParms = getCallParms();
		if (fldCallParms != null) {
			write.sub("callParms", false);
			for (CallParmsArray sub : fldCallParms)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldFor = getFor();
		if (fldFor != null && fldFor.getRec() != 0) {
			write.sub("for", false);
			fldFor.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldForExpr = getForExpr();
		if (fldForExpr != null && fldForExpr.getRec() != 0) {
			write.sub("forExpr", false);
			fldForExpr.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldSort = getSort();
		if (fldSort != null && fldSort.getRec() != 0) {
			write.sub("sort", false);
			fldSort.output(write, iterate - 1);
			write.endSub();
		}
		SortParmsArray fldSortParms = getSortParms();
		if (fldSortParms != null) {
			write.sub("sortParms", false);
			for (SortParmsArray sub : fldSortParms)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		Expr fldIf = getIf();
		if (fldIf != null && fldIf.getRec() != 0) {
			write.sub("if", false);
			fldIf.output(write, iterate - 1);
			write.endSub();
		}
		IfTrueArray fldIfTrue = getIfTrue();
		if (fldIfTrue != null) {
			write.sub("ifTrue", false);
			for (IfTrueArray sub : fldIfTrue)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		IfFalseArray fldIfFalse = getIfFalse();
		if (fldIfFalse != null) {
			write.sub("ifFalse", false);
			for (IfFalseArray sub : fldIfFalse)
				sub.output(write, iterate - 1);
			write.endSub();
		}
		write.field("listenSource", getListenSource(), false);
		write.field("listemNr", getListemNr(), false);
	}

	default Object getOperator(int field) {
		if (field >= 25 && field <= 27)
			return getResultType(field - 25);
		switch (field) {
		case 1:
			return getOperation();
		case 2:
			return getFunction();
		case 3:
			return getFnParm1();
		case 4:
			return getFnParm2();
		case 5:
			return getConExpr();
		case 6:
			return getConTrue();
		case 7:
			return getConFalse();
		case 8:
			return getNumber();
		case 9:
			return getFloat();
		case 10:
			return getString();
		case 14:
			return isBoolean();
		case 15:
			return getMacro();
		case 17:
			return getFor();
		case 18:
			return getForExpr();
		case 19:
			return getSort();
		case 21:
			return getIf();
		case 24:
			return getListenSource();
		case 25:
			return getListemNr();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateOperator(int field, @SuppressWarnings("unused") Object... key) {
		if (field >= 25 && field <= 27)
			return iterateResultType(field - 25);
		switch (field) {
		case 11:
			return getArray();
		case 12:
			return getAppend();
		case 13:
			return getObject();
		case 16:
			return getCallParms();
		case 20:
			return getSortParms();
		case 22:
			return getIfTrue();
		case 23:
			return getIfFalse();
		default:
			return null;
		}
	}

	default FieldType typeOperator(int field) {
		if (field >= 25 && field <= 27)
			return typeResultType(field - 25);
		switch (field) {
		case 1:
			return FieldType.STRING;
		case 2:
			return FieldType.STRING;
		case 3:
			return FieldType.OBJECT;
		case 4:
			return FieldType.OBJECT;
		case 5:
			return FieldType.OBJECT;
		case 6:
			return FieldType.OBJECT;
		case 7:
			return FieldType.OBJECT;
		case 8:
			return FieldType.LONG;
		case 9:
			return FieldType.FLOAT;
		case 10:
			return FieldType.STRING;
		case 11:
			return FieldType.ITERATE;
		case 12:
			return FieldType.ITERATE;
		case 13:
			return FieldType.ITERATE;
		case 14:
			return FieldType.BOOLEAN;
		case 15:
			return FieldType.OBJECT;
		case 16:
			return FieldType.ITERATE;
		case 17:
			return FieldType.OBJECT;
		case 18:
			return FieldType.OBJECT;
		case 19:
			return FieldType.OBJECT;
		case 20:
			return FieldType.ITERATE;
		case 21:
			return FieldType.OBJECT;
		case 22:
			return FieldType.ITERATE;
		case 23:
			return FieldType.ITERATE;
		case 24:
			return FieldType.STRING;
		case 25:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	default String nameOperator(int field) {
		if (field >= 25 && field <= 27)
			return nameResultType(field - 25);
		switch (field) {
		case 1:
			return "operation";
		case 2:
			return "function";
		case 3:
			return "fnParm1";
		case 4:
			return "fnParm2";
		case 5:
			return "conExpr";
		case 6:
			return "conTrue";
		case 7:
			return "conFalse";
		case 8:
			return "number";
		case 9:
			return "float";
		case 10:
			return "string";
		case 11:
			return "array";
		case 12:
			return "append";
		case 13:
			return "object";
		case 14:
			return "boolean";
		case 15:
			return "macro";
		case 16:
			return "callParms";
		case 17:
			return "for";
		case 18:
			return "forExpr";
		case 19:
			return "sort";
		case 20:
			return "sortParms";
		case 21:
			return "if";
		case 22:
			return "ifTrue";
		case 23:
			return "ifFalse";
		case 24:
			return "listenSource";
		case 25:
			return "listemNr";
		default:
			return null;
		}
	}
}
