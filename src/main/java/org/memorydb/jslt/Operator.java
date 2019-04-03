package org.memorydb.jslt;

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
	Store store();

	@Override
	default int resulttypePosition() {
		return operatorPosition() + 13;
	}

	@Override
	Operator parseKey(Parser parser);

	default ChangeOperator changeOperator() {
		if (this instanceof ObjectArray)
			return (ObjectArray) this;
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
		if (this instanceof MparmsArray)
			return (MparmsArray) this;
		if (this instanceof CodeArray)
			return (CodeArray) this;
		if (this instanceof Expr)
			return new ChangeExpr((Expr) this);
		return null;
	}

	public enum Operation {
		FUNCTION, CONDITION, NUMBER, FLOAT, STRING, ARRAY, OBJECT, BOOLEAN, APPEND, NULL, CALL, FILTER, SORT, IF, CURRENT, RUNNING, READ, VARIABLE;

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

	@FieldData(name = "operation", type = "ENUMERATE", enumerate = { "FUNCTION", "CONDITION", "NUMBER", "FLOAT", "STRING", "ARRAY", "OBJECT", "BOOLEAN", "APPEND", "NULL", "CALL", "FILTER", "SORT", "IF", "CURRENT", "RUNNING", "READ", "VARIABLE" }, condition = true, mandatory = false)
	default Operation getOperation() {
		int data = rec() == 0 ? 0 : store().getByte(rec(), operatorPosition() + 0) & 63;
		if (data <= 0)
			return null;
		return Operation.values()[data - 1];
	}

	public enum Function {
		NEG, ADD, MIN, MUL, DIV, MOD, POW, EQ, NE, LT, GT, LE, GE, AND, OR, NOT, FIRST, LAST, INDEX, LENGTH, NUMBER, FLOAT, STRING, BOOLEAN, NAME, TYPE, ELEMENT, PER, FOR, EACH, LAYOUT;

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

	@FieldData(name = "function", type = "ENUMERATE", enumerate = { "NEG", "ADD", "MIN", "MUL", "DIV", "MOD", "POW", "EQ", "NE", "LT", "GT", "LE", "GE", "AND", "OR", "NOT", "FIRST", "LAST", "INDEX", "LENGTH", "NUMBER", "FLOAT", "STRING", "BOOLEAN", "NAME", "TYPE", "ELEMENT", "PER", "FOR", "EACH", "LAYOUT" }, when = "FUNCTION", mandatory = true)
	default Function getFunction() {
		int data = getOperation() != Operation.FUNCTION ? 0 : store().getByte(rec(), operatorPosition() + 1) & 127;
		if (data <= 0)
			return null;
		return Function.values()[data - 1];
	}

	@FieldData(name = "fnParm1", type = "OBJECT", related = Expr.class, when = "FUNCTION", mandatory = false)
	default Expr getFnParm1() {
		return new Expr(store(), getOperation() != Operation.FUNCTION ? 0 : store().getInt(rec(), operatorPosition() + 2));
	}

	@FieldData(name = "fnParm2", type = "OBJECT", related = Expr.class, when = "FUNCTION", mandatory = false)
	default Expr getFnParm2() {
		return new Expr(store(), getOperation() != Operation.FUNCTION ? 0 : store().getInt(rec(), operatorPosition() + 6));
	}

	@FieldData(name = "conExpr", type = "OBJECT", related = Expr.class, when = "CONDITION", mandatory = false)
	default Expr getConExpr() {
		return new Expr(store(), getOperation() != Operation.CONDITION ? 0 : store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "conTrue", type = "OBJECT", related = Expr.class, when = "CONDITION", mandatory = false)
	default Expr getConTrue() {
		return new Expr(store(), getOperation() != Operation.CONDITION ? 0 : store().getInt(rec(), operatorPosition() + 5));
	}

	@FieldData(name = "conFalse", type = "OBJECT", related = Expr.class, when = "CONDITION", mandatory = false)
	default Expr getConFalse() {
		return new Expr(store(), getOperation() != Operation.CONDITION ? 0 : store().getInt(rec(), operatorPosition() + 9));
	}

	@FieldData(name = "number", type = "LONG", when = "NUMBER", mandatory = false)
	default long getNumber() {
		return getOperation() != Operation.NUMBER ? Long.MIN_VALUE : store().getLong(rec(), operatorPosition() + 1);
	}

	@FieldData(name = "float", type = "FLOAT", when = "FLOAT", mandatory = false)
	default double getFloat() {
		return getOperation() != Operation.FLOAT ? Double.NaN : Double.longBitsToDouble(store().getLong(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "string", type = "STRING", when = "STRING", mandatory = false)
	default String getString() {
		return getOperation() != Operation.STRING ? null : store().getString(store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "array", type = "ARRAY", related = ArrayArray.class, when = "ARRAY", mandatory = false)
	default ArrayArray getArray() {
		return getOperation() != Operation.ARRAY ? null : new ArrayArray(this, -1);
	}

	default ArrayArray getArray(int index) {
		return getOperation() != Operation.ARRAY ? new ArrayArray(store(), 0, -1) : new ArrayArray(this, index);
	}

	default ArrayArray addArray() {
		return getOperation() != Operation.ARRAY ? new ArrayArray(store(), 0, -1) : getArray().add();
	}

	@FieldData(name = "append", type = "ARRAY", related = AppendArray.class, when = "APPEND", mandatory = false)
	default AppendArray getAppend() {
		return getOperation() != Operation.APPEND ? null : new AppendArray(this, -1);
	}

	default AppendArray getAppend(int index) {
		return getOperation() != Operation.APPEND ? new AppendArray(store(), 0, -1) : new AppendArray(this, index);
	}

	default AppendArray addAppend() {
		return getOperation() != Operation.APPEND ? new AppendArray(store(), 0, -1) : getAppend().add();
	}

	@FieldData(name = "object", type = "ARRAY", related = ObjectArray.class, when = "OBJECT", mandatory = false)
	default ObjectArray getObject() {
		return getOperation() != Operation.OBJECT ? null : new ObjectArray(this, -1);
	}

	default ObjectArray getObject(int index) {
		return getOperation() != Operation.OBJECT ? new ObjectArray(store(), 0, -1) : new ObjectArray(this, index);
	}

	default ObjectArray addObject() {
		return getOperation() != Operation.OBJECT ? new ObjectArray(store(), 0, -1) : getObject().add();
	}

	@FieldData(name = "boolean", type = "BOOLEAN", when = "BOOLEAN", mandatory = false)
	default boolean isBoolean() {
		return getOperation() != Operation.BOOLEAN ? false : (store().getByte(rec(), operatorPosition() + 1) & 1) > 0;
	}

	@FieldData(name = "macro", type = "RELATION", related = Macro.class, when = "CALL", mandatory = false)
	default Macro getMacro() {
		return new Macro(store(), getOperation() != Operation.CALL ? 0 : store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "callParms", type = "ARRAY", related = CallParmsArray.class, when = "CALL", mandatory = false)
	default CallParmsArray getCallParms() {
		return getOperation() != Operation.CALL ? null : new CallParmsArray(this, -1);
	}

	default CallParmsArray getCallParms(int index) {
		return getOperation() != Operation.CALL ? new CallParmsArray(store(), 0, -1) : new CallParmsArray(this, index);
	}

	default CallParmsArray addCallParms() {
		return getOperation() != Operation.CALL ? new CallParmsArray(store(), 0, -1) : getCallParms().add();
	}

	@FieldData(name = "filter", type = "OBJECT", related = Expr.class, when = "FILTER", mandatory = false)
	default Expr getFilter() {
		return new Expr(store(), getOperation() != Operation.FILTER ? 0 : store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "filterDeep", type = "BOOLEAN", related = Expr.class, when = "FILTER", mandatory = false)
	default boolean isFilterDeep() {
		return getOperation() != Operation.FILTER ? false : (store().getByte(rec(), operatorPosition() + 5) & 1) > 0;
	}

	@FieldData(name = "filterExpr", type = "OBJECT", related = Expr.class, when = "FILTER", mandatory = false)
	default Expr getFilterExpr() {
		return new Expr(store(), getOperation() != Operation.FILTER ? 0 : store().getInt(rec(), operatorPosition() + 6));
	}

	@FieldData(name = "sort", type = "OBJECT", related = Expr.class, when = "SORT", mandatory = false)
	default Expr getSort() {
		return new Expr(store(), getOperation() != Operation.SORT ? 0 : store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "sortParms", type = "ARRAY", related = SortParmsArray.class, when = "SORT", mandatory = false)
	default SortParmsArray getSortParms() {
		return getOperation() != Operation.SORT ? null : new SortParmsArray(this, -1);
	}

	default SortParmsArray getSortParms(int index) {
		return getOperation() != Operation.SORT ? new SortParmsArray(store(), 0, -1) : new SortParmsArray(this, index);
	}

	default SortParmsArray addSortParms() {
		return getOperation() != Operation.SORT ? new SortParmsArray(store(), 0, -1) : getSortParms().add();
	}

	@FieldData(name = "if", type = "OBJECT", related = Expr.class, when = "IF", mandatory = false)
	default Expr getIf() {
		return new Expr(store(), getOperation() != Operation.IF ? 0 : store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "ifTrue", type = "ARRAY", related = IfTrueArray.class, when = "IF", mandatory = false)
	default IfTrueArray getIfTrue() {
		return getOperation() != Operation.IF ? null : new IfTrueArray(this, -1);
	}

	default IfTrueArray getIfTrue(int index) {
		return getOperation() != Operation.IF ? new IfTrueArray(store(), 0, -1) : new IfTrueArray(this, index);
	}

	default IfTrueArray addIfTrue() {
		return getOperation() != Operation.IF ? new IfTrueArray(store(), 0, -1) : getIfTrue().add();
	}

	@FieldData(name = "ifFalse", type = "ARRAY", related = IfFalseArray.class, when = "IF", mandatory = false)
	default IfFalseArray getIfFalse() {
		return getOperation() != Operation.IF ? null : new IfFalseArray(this, -1);
	}

	default IfFalseArray getIfFalse(int index) {
		return getOperation() != Operation.IF ? new IfFalseArray(store(), 0, -1) : new IfFalseArray(this, index);
	}

	default IfFalseArray addIfFalse() {
		return getOperation() != Operation.IF ? new IfFalseArray(store(), 0, -1) : getIfFalse().add();
	}

	@FieldData(name = "listenSource", type = "STRING", when = "READ", mandatory = false)
	default String getListenSource() {
		return getOperation() != Operation.READ ? null : store().getString(store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "listemNr", type = "INTEGER", when = "READ", mandatory = false)
	default int getListemNr() {
		return getOperation() != Operation.READ ? Integer.MIN_VALUE : store().getInt(rec(), operatorPosition() + 5);
	}

	@FieldData(name = "varName", type = "STRING", when = "VARIABLE", mandatory = false)
	default String getVarName() {
		return getOperation() != Operation.VARIABLE ? null : store().getString(store().getInt(rec(), operatorPosition() + 1));
	}

	@FieldData(name = "varNr", type = "INTEGER", when = "VARIABLE", mandatory = false)
	default int getVarNr() {
		return getOperation() != Operation.VARIABLE ? Integer.MIN_VALUE : store().getInt(rec(), operatorPosition() + 5);
	}

	default void outputOperator(Write write, int iterate) {
		if (rec() == 0 || iterate <= 0)
			return;
		write.field("operation", getOperation());
		write.field("function", getFunction());
		Expr fldFnParm1 = getFnParm1();
		if (fldFnParm1 != null && fldFnParm1.rec() != 0) {
			write.sub("fnParm1");
			fldFnParm1.output(write, iterate);
			write.endSub();
		}
		Expr fldFnParm2 = getFnParm2();
		if (fldFnParm2 != null && fldFnParm2.rec() != 0) {
			write.sub("fnParm2");
			fldFnParm2.output(write, iterate);
			write.endSub();
		}
		Expr fldConExpr = getConExpr();
		if (fldConExpr != null && fldConExpr.rec() != 0) {
			write.sub("conExpr");
			fldConExpr.output(write, iterate);
			write.endSub();
		}
		Expr fldConTrue = getConTrue();
		if (fldConTrue != null && fldConTrue.rec() != 0) {
			write.sub("conTrue");
			fldConTrue.output(write, iterate);
			write.endSub();
		}
		Expr fldConFalse = getConFalse();
		if (fldConFalse != null && fldConFalse.rec() != 0) {
			write.sub("conFalse");
			fldConFalse.output(write, iterate);
			write.endSub();
		}
		write.field("number", getNumber());
		write.field("float", getFloat());
		write.field("string", getString());
		ArrayArray fldArray = getArray();
		if (fldArray != null) {
			write.sub("array");
			for (ArrayArray sub : fldArray)
				sub.output(write, iterate);
			write.endSub();
		}
		AppendArray fldAppend = getAppend();
		if (fldAppend != null) {
			write.sub("append");
			for (AppendArray sub : fldAppend)
				sub.output(write, iterate);
			write.endSub();
		}
		ObjectArray fldObject = getObject();
		if (fldObject != null) {
			write.sub("object");
			for (ObjectArray sub : fldObject)
				sub.output(write, iterate);
			write.endSub();
		}
		if (getOperation() == Operation.BOOLEAN)
			write.field("boolean", isBoolean());
		write.field("macro", getMacro());
		CallParmsArray fldCallParms = getCallParms();
		if (fldCallParms != null) {
			write.sub("callParms");
			for (CallParmsArray sub : fldCallParms)
				sub.output(write, iterate);
			write.endSub();
		}
		Expr fldFilter = getFilter();
		if (fldFilter != null && fldFilter.rec() != 0) {
			write.sub("filter");
			fldFilter.output(write, iterate);
			write.endSub();
		}
		if (getOperation() == Operation.FILTER)
			write.field("filterDeep", isFilterDeep());
		Expr fldFilterExpr = getFilterExpr();
		if (fldFilterExpr != null && fldFilterExpr.rec() != 0) {
			write.sub("filterExpr");
			fldFilterExpr.output(write, iterate);
			write.endSub();
		}
		Expr fldSort = getSort();
		if (fldSort != null && fldSort.rec() != 0) {
			write.sub("sort");
			fldSort.output(write, iterate);
			write.endSub();
		}
		SortParmsArray fldSortParms = getSortParms();
		if (fldSortParms != null) {
			write.sub("sortParms");
			for (SortParmsArray sub : fldSortParms)
				sub.output(write, iterate);
			write.endSub();
		}
		Expr fldIf = getIf();
		if (fldIf != null && fldIf.rec() != 0) {
			write.sub("if");
			fldIf.output(write, iterate);
			write.endSub();
		}
		IfTrueArray fldIfTrue = getIfTrue();
		if (fldIfTrue != null) {
			write.sub("ifTrue");
			for (IfTrueArray sub : fldIfTrue)
				sub.output(write, iterate);
			write.endSub();
		}
		IfFalseArray fldIfFalse = getIfFalse();
		if (fldIfFalse != null) {
			write.sub("ifFalse");
			for (IfFalseArray sub : fldIfFalse)
				sub.output(write, iterate);
			write.endSub();
		}
		write.field("listenSource", getListenSource());
		write.field("listemNr", getListemNr());
		write.field("varName", getVarName());
		write.field("varNr", getVarNr());
	}

	default Object getOperator(int field) {
		if (field >= 28 && field <= 30)
			return getResultType(field - 28);
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
			return getFilter();
		case 18:
			return isFilterDeep();
		case 19:
			return getFilterExpr();
		case 20:
			return getSort();
		case 22:
			return getIf();
		case 25:
			return getListenSource();
		case 26:
			return getListemNr();
		case 27:
			return getVarName();
		case 28:
			return getVarNr();
		default:
			return null;
		}
	}

	default Iterable<? extends RecordInterface> iterateOperator(int field, @SuppressWarnings("unused") Object... key) {
		if (field >= 28 && field <= 30)
			return iterateResultType(field - 28);
		switch (field) {
		case 11:
			return getArray();
		case 12:
			return getAppend();
		case 13:
			return getObject();
		case 16:
			return getCallParms();
		case 21:
			return getSortParms();
		case 23:
			return getIfTrue();
		case 24:
			return getIfFalse();
		default:
			return null;
		}
	}

	default FieldType typeOperator(int field) {
		if (field >= 28 && field <= 30)
			return typeResultType(field - 28);
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
			return FieldType.ARRAY;
		case 12:
			return FieldType.ARRAY;
		case 13:
			return FieldType.ARRAY;
		case 14:
			return FieldType.BOOLEAN;
		case 15:
			return FieldType.OBJECT;
		case 16:
			return FieldType.ARRAY;
		case 17:
			return FieldType.OBJECT;
		case 18:
			return FieldType.BOOLEAN;
		case 19:
			return FieldType.OBJECT;
		case 20:
			return FieldType.OBJECT;
		case 21:
			return FieldType.ARRAY;
		case 22:
			return FieldType.OBJECT;
		case 23:
			return FieldType.ARRAY;
		case 24:
			return FieldType.ARRAY;
		case 25:
			return FieldType.STRING;
		case 26:
			return FieldType.INTEGER;
		case 27:
			return FieldType.STRING;
		case 28:
			return FieldType.INTEGER;
		default:
			return null;
		}
	}

	default String nameOperator(int field) {
		if (field >= 28 && field <= 30)
			return nameResultType(field - 28);
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
			return "filter";
		case 18:
			return "filterDeep";
		case 19:
			return "filterExpr";
		case 20:
			return "sort";
		case 21:
			return "sortParms";
		case 22:
			return "if";
		case 23:
			return "ifTrue";
		case 24:
			return "ifFalse";
		case 25:
			return "listenSource";
		case 26:
			return "listemNr";
		case 27:
			return "varName";
		case 28:
			return "varNr";
		default:
			return null;
		}
	}
}
