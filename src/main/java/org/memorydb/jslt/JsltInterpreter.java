package org.memorydb.jslt;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.memorydb.handler.StringText;
import org.memorydb.handler.StringWriter;
import org.memorydb.handler.Text;
import org.memorydb.handler.Writer;
import org.memorydb.jslt.Macro.IndexMacros;
import org.memorydb.jslt.Operator.Function;
import org.memorydb.jslt.Operator.Operation;
import org.memorydb.jslt.ResultType.Type;
import org.memorydb.structure.RecordInterface;
import org.memorydb.structure.RecordInterface.FieldType;
import org.memorydb.structure.Store;

public class JsltInterpreter {
	private int index;
	private Object current;
	private boolean first;
	private boolean last;
	private String curName;
	private RecordInterface data;
	@SuppressWarnings("unused")
	private List<Pair> pairs = new ArrayList<>();
	private List<Object> stack = new ArrayList<>(100);
	private int stackFrame = 0;
	private RecordInterface curFor = null;
	private Object running = null;

	public static String interpret(Store jsltStore, RecordInterface data) {
		JsltInterpreter inter = new JsltInterpreter();
		inter.data = data;
		Writer write = new StringWriter();
		Macro macro = new Macro(jsltStore);
		IndexMacros macros = macro.new IndexMacros("main");
		macro.setRec(macros.search());
		for (CodeArray code : macro.getAlternatives(0).getCode())
			inter.show(write, inter.inter(code));
		return write.toString();
	}

	public void setCurrent(Object current) {
		this.current = current;
	}

	public void setCurName(String curName) {
		this.curName = curName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setFirst(boolean first) {
		this.first = first;
	}

	public void setLast(boolean last) {
		this.last = last;
	}

	public Object inter(Operator code) {
		if (code.getOperation() == null)
			return null;
		switch (code.getOperation()) {
		case ARRAY:
			return new InterArray(this, code.getArray());
		case OBJECT:
			return new InterObject(this, code.getObject());
		case APPEND:
			StringBuilder bld = new StringBuilder();
			for (AppendArray app : code.getAppend())
				bld.append(getString(inter(app)));
			return bld.toString();
		case BOOLEAN:
			return code.isBoolean();
		case CALL:
			return macro(code);
		case CONDITION:
			return inter((Boolean) inter(code.getConExpr()) ? code.getConTrue() : code.getConFalse());
		case FLOAT:
			return code.getFloat();
		case FUNCTION:
			return doFunction(code);
		case RUNNING:
			return running;
		case FILTER:
			return new InterFilter(this, code.getFilterExpr(), (RecordInterface) inter(code.getFilter()));
		case IF:
			return null; // TODO implement me
		case NULL:
			return null;
		case NUMBER:
			return code.getNumber();
		case SORT:
			return new InterSort(this, code.getSortParms(), (RecordInterface) inter(code.getSort()));
		case STRING:
			return code.getString();
		case VARIABLE:
			return stack.get(stackFrame + code.getVarNr());
		case CURRENT:
			if (current instanceof Operator)
				return inter((Operator) current);
			return current;
		case READ:
			return data;
		default:
			return null;
		}
	}

	private Object macro(Operator code) {
		Macro macro = code.getMacro();
		if (macro.getName().equals("slice")) {
			Object parmData = inter(new CallParmsArray(code.getCallParms(), 0));
			if (parmData instanceof RecordInterface)
				return new InterSlice(this, (RecordInterface) parmData, code.getCallParms());
			if (parmData instanceof String)
				return subString((String) parmData, code.getCallParms());
			throw new RuntimeException("Slice not implemented with type " + type(parmData));
		}
		int stackF = stack.size();
		int parms = code.getCallParms().getSize();
		for (CallParmsArray parm : code.getCallParms())
			stack.add(inter(parm));
		return findAlternative(macro, stackF, parms);
	}

	private Object findAlternative(Macro macro, int stackF, int parms) {
		int oldFrame = stackFrame;
		for (Alternative alt : macro.getAlternatives()) {
			if (alt.getParameters().getSize() != parms)
				continue;
			int pnr = 0;
			boolean found = true;
			int newFrame = stack.size();
			stackFrame = newFrame;
			for (ParametersArray parm : alt.getParameters()) {
				Object obj = stack.get(stackF + pnr);
				found = testParm(parm, obj);
				pnr++;
				if (!found)
					break;
			}
			Expr ifExpr = alt.getIf();
			if (ifExpr != null) {
				Object ifResult = inter(ifExpr);
				if (ifResult instanceof Boolean && (Boolean) ifResult == false)
					found = false;
			}
			if (found) {
				Object res = inter(alt.getCode().iterator().next());
				stackFrame = oldFrame;
				while (stack.size() > stackF)
					stack.remove(stack.size() - 1);
				return res;
			}
			while (stack.size() > newFrame)
				stack.remove(stack.size() - 1);
			stackFrame = newFrame;
		}
		while (stack.size() > stackF)
			stack.remove(stack.size() - 1);
		stackFrame = oldFrame;
		return null;
	}

	private boolean testParm(Match parm, Object obj) {
		switch (parm.getType()) {
		case BOOLEAN:
			if (!(obj instanceof Boolean) || (Boolean) obj != parm.isBoolean())
				return false;
			break;
		case FLOAT:
			if (!(obj instanceof Double) || (Double) obj != parm.getFloat())
				return false;
			break;
		case NULL:
			if (obj != null)
				return false;
			break;
		case NUMBER:
			if (!(obj instanceof Long) || (Long) obj != parm.getNumber())
				return false;
			break;
		case OBJECT:
			if (!(obj instanceof RecordInterface) || ((RecordInterface) obj).type() != FieldType.OBJECT)
				return false;
			break;
		case ARRAY:
			if (obj instanceof RecordInterface && ((RecordInterface) obj).type() == FieldType.ARRAY)
				return arrayMatch(parm, (RecordInterface) obj);
			else if (obj instanceof String)
				return textMatch(parm, new StringText((String) obj));
			else if (obj instanceof Text)
				return textMatch(parm, (Text) obj);
			return false;
		case STRING:
			if (!(obj instanceof String) || !parm.getString().equals(obj))
				return false;
			break;
		case VARIABLE:
			if (!matches(obj, parm.getVariable().getType()))
				return false;
			stack.add(obj);
			break;
		case CONSTANT:
			throw new RuntimeException("Constant");
		case MULTIPLE:
			// multiple outside or the last element of an array
			while (testParm(parm.getMmatch(), obj)) {
				throw new RuntimeException("Not implemented yet");
			}
			break;
		case MACRO:
			throw new RuntimeException("macro");
		default:
			break;
		}
		return true;
	}

	private boolean arrayMatch(Match parm, RecordInterface arr) {
		int elms = 0;
		int aElm = arr.next(-1);
		Iterator<MarrayArray> iterator = parm.getMarray().iterator();
		List<Match> multiple = new ArrayList<>(); // remember multiple match elements
		boolean notLast = iterator.hasNext();
		while (notLast) {
			if (aElm < 0)
				return false; // beyond the last array element
			MarrayArray elm = iterator.next();
			notLast = iterator.hasNext();
			if (notLast && elm.getType() == Match.Type.MULTIPLE) {
				multiple.add(elm);
			} else if (!multiple.isEmpty()) {
				for (int m = multiple.size() - 1; m >= 0; m--) {
					System.out.println("here");
				}
			} else if (!notLast) { // last element
				if (elm.getType() == Match.Type.VARIABLE
						&& (elm.getVariable().getType() == Type.NULL || elm.getVariable().getType() == Type.ARRAY)) {
					stack.add(new SubArray(arr, aElm, elms));
					return true; // match the rest of the array
				}
				if (!testParm(elm, arr.get(aElm)))
					return false; // next element is not of the correct type
				aElm = arr.next(aElm);
				elms++;
				// TODO the end of array test should not be validated on a CALL type variable
				if (arr.type(aElm) != null)
					return false; // not at the end of the array yet
			} else {
				if (!testParm(elm, arr.get(aElm)))
					return false; // next element is not of the correct type
				multiple.clear();
				aElm = arr.next(aElm);
				elms++;
			}
		}
		return true;
	}

	private boolean textMatch(Match parm, Text obj) {
		Iterator<MarrayArray> iterator = parm.getMarray().iterator();
		List<Match> multiple = new ArrayList<>(); // remember multiple match elements
		boolean notLast = iterator.hasNext();
		int pos = obj.addPos();
		while (notLast) {
			MarrayArray elm = iterator.next();
			notLast = iterator.hasNext();
			if (elm.getType() == Match.Type.MULTIPLE) {
				multiple.add(elm.getMmatch());
			} else if (!multiple.isEmpty()) {
				if (!textParm(elm, obj)) {
					for (int i = multiple.size() - 1; i > -1; i--) {
						boolean found = false;
						while (true) {
							if (textParm(multiple.get(i), obj))
								found = true;
							else
								break;
						}
						if (found)
							break;
					}
					textParm(elm, obj);
				}
				if (!notLast && !obj.end()) {
					obj.toPos(pos);
					return false; // not at the end of the array yet
				}
			} else if (!notLast) { // last element
				if (elm.getType() == Match.Type.VARIABLE
						&& (elm.getVariable().getType() == Type.NULL || elm.getVariable().getType() == Type.ARRAY)) {
					stack.add(obj.tail());
					obj.freePos(pos);
					return true; // match the rest of the array
				}
				if (!textParm(elm, obj)) {
					obj.toPos(pos);
					return false; // next element is not of the correct type
				}
				// TODO the end of array test should not be validated on a CALL type variable
				if (!obj.end()) {
					obj.toPos(pos);
					return false; // not at the end of the array yet
				}
			} else {
				if (!textParm(elm, obj)) {
					obj.toPos(pos);
					return false; // next element is not of the correct type
				}
			}
		}
		obj.freePos(pos);
		return true;
	}

	private boolean textParm(Match parm, Text on) {
		switch (parm.getType()) {
		case BOOLEAN:
			return on.match(parm.isBoolean() ? "true" : "false");
		case NUMBER:
		case FLOAT:
		case OBJECT:
		case ARRAY:
			throw new RuntimeException("Not implemented yet");
		case NULL:
			return on.match("null");
		case STRING:
			return on.match(parm.getString());
		case VARIABLE:
			if (on.end())
				return false;
			MatchObject vmatch = parm.getVmatch();
			if (vmatch != null && vmatch.getRec() != 0) {
				int pos = on.addPos();
				boolean res = textParm(vmatch, on);
				stack.add(on.substring(pos));
				on.freePos(pos);
				return res;
			} else {
				stack.add(on.readChar() + "");
				return true;
			}
		case CONSTANT:
			Object object = stack.get(stackFrame + parm.getConstant());
			return on.match(object == null ? "null" : object.toString());
		case MULTIPLE:
			while (textParm(parm.getMmatch(), on))
				// nothing
			return true;
		case MACRO:
			int stackF = stack.size();
			int parms = parm.getMparms().getSize() + 1;
			for (MparmsArray p : parm.getMparms()) {
				Object obj = inter(p);
				stack.add(obj);
			}
			int pos = on.addPos();
			stack.add(on.readChar() + "");
			Object res = findAlternative(parm.getMacro(), stackF, parms);
			if (res instanceof Boolean && ((Boolean) res)) {
				on.freePos(pos);
				return true;
			}
			on.toPos(pos);
		}
		return false;
	}

	/**
	 * An array that represents a sub array starting from a specified position
	 */
	class SubArray implements RecordInterface {
		private final RecordInterface arr;
		private final int elm;
		private final int curSize;

		public SubArray(RecordInterface arr, int elm, int curSize) {
			if (arr instanceof SubArray) {
				SubArray sub = (SubArray) arr;
				arr = sub.arr;
				curSize += sub.curSize;
			}
			this.arr = arr;
			this.elm = elm;
			this.curSize = curSize;
		}

		@Override
		public int getSize() {
			return arr.getSize() - curSize;
		}

		@Override
		public int next(int field) {
			if (field < 0)
				return elm; // first element
			return next(field);
		}

		@Override
		public String name(int field) {
			return arr.name(field);
		}

		@Override
		public FieldType type(int field) {
			return arr.type(field);
		}

		@Override
		public Object get(int field) {
			return arr.get(field);
		}

		@Override
		public String toString() {
			StringBuilder bld = new StringBuilder();
			bld.append("[");
			int e = elm;
			while (type(e) != null) {
				int n = next(e);
				bld.append(get(e));
				if (n >= 0)
					bld.append(", ");
				e = n;
			}
			bld.append("]");
			return bld.toString();
		}
	}

	private boolean matches(Object obj, Type type) {
		switch (type) {
		case ARRAY:
			return obj instanceof RecordInterface && ((RecordInterface) obj).type() == FieldType.ARRAY;
		case BOOLEAN:
			return obj instanceof Boolean;
		case FLOAT:
			return obj instanceof Double || obj instanceof Long;
		case NULL:
			return true;
		case NUMBER:
			return obj instanceof Long;
		case OBJECT:
			return obj instanceof RecordInterface && ((RecordInterface) obj).type() == FieldType.OBJECT;
		case STRING:
			return obj instanceof String;
		case STRUCTURE:
			return true;
		default:
			return false;
		}
	}

	private String subString(String str, CallParmsArray callParms) {
		long[] parms = new long[callParms.getSize() - 1];
		for (int p = 0; p < callParms.getSize() - 1; p++)
			parms[p] = getNumber(inter(new CallParmsArray(callParms, p + 1)));
		StringBuilder res = new StringBuilder();
		int size = str.length();
		for (int p = 0; p < parms.length; p += 3) {
			long start = parms[p];
			long stop = parms[p + 1];
			long step = parms[p + 2];
			if (step == Long.MIN_VALUE)
				step = 1;
			if (step < 0) {
				if (start == Long.MIN_VALUE)
					start = size - 1;
				if (start < 0)
					start += size;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = -1;
				for (int i = (int) start; i > stop; i += step)
					res.append(str.charAt(i));
			} else {
				if (start == Long.MIN_VALUE)
					start = 0;
				else if (start < 0)
					start += size;
				if (stop == Long.MAX_VALUE)
					stop = start + 1;
				if (stop < 0 && stop != Long.MIN_VALUE)
					stop += size;
				if (stop == Long.MIN_VALUE)
					stop = size;
				for (int i = (int) start; i < stop; i += step)
					res.append(str.charAt(i));
			}
		}
		return res.toString();
	}

	private void iterate(Writer write, RecordInterface rec) {
		FieldType type = rec.type();
		if (type == FieldType.OBJECT)
			iterateObject(3, write, rec);
		else if (type == FieldType.ARRAY)
			iterateArray(3, write, rec);
	}

	private void show(Writer write, Object obj) {
		if (obj instanceof RecordInterface)
			iterate(write, (RecordInterface) obj);
		else
			write.element(obj);
	}

	private void iterateObject(int maxDepth, Writer write, RecordInterface rec) {
		if (maxDepth <= 0 || rec == null || !rec.exists())
			return;
		write.startObject();
		for (int field = rec.next(-1); field >= 0; field = rec.next(field)) {
			FieldType type = rec.type(field);
			write.field(rec.name(field));
			if (type == FieldType.OBJECT)
				iterateObject(maxDepth - 1, write, (RecordInterface) rec.get(field));
			else if (type == FieldType.ARRAY)
				iterateArray(maxDepth, write, (RecordInterface) rec.get(field));
			else
				write.element(rec.get(field));
		}
		write.endObject();
	}

	private void iterateArray(int maxDepth, Writer write, RecordInterface rec) {
		write.startArray();
		for (int field = rec.next(-1); field >= 0; field = rec.next(field)) {
			FieldType type = rec.type(field);
			if (type == FieldType.OBJECT)
				iterateObject(maxDepth, write, (RecordInterface) rec.get(field));
			else if (type == FieldType.ARRAY)
				iterateArray(maxDepth, write, (RecordInterface) rec.get(field));
			else
				write.element(rec.get(field));
		}
		write.endArray();
	}

	@SuppressWarnings("unused")
	private class Pair {
		final Operator code;
		int op; // <0 out of scope, 1:default, 2:override, 3:remove

		public Pair(Operator code, int op) {
			this.code = code;
			this.op = op;
		}
	}

	private void iterate(int maxDepth, Writer write, String name, RecordInterface rec, int ops) {
		if (maxDepth <= 0 || !rec.exists())
			return;
		int f = rec.next(-1);
		if (f == 0) {
			nonObject(maxDepth, write, rec, 0, ops);
			return;
		}
		if (name != null)
			write.field(name);
		write.startObject();
		for (int field = f; field >= 0; field = rec.next(field)) {
			if (rec.type(field) == FieldType.OBJECT)
				iterate(maxDepth - 1, write, rec.name(field), (RecordInterface) rec.get(field), ops);
			else
				nonObject(maxDepth, write, rec, field, ops);
		}
		write.endObject();
	}

	private void nonObject(int maxDepth, Writer write, RecordInterface rec, int field, int ops) {
		String name = rec.name(field);
		if (name != null)
			write.field(name);
		if (rec.type(field) == FieldType.ARRAY) {
			write.startArray();
			Iterable<? extends RecordInterface> iterate = rec.iterate(field);
			if (iterate != null) {
				for (RecordInterface sub : iterate)
					iterate(maxDepth, write, null, sub, ops);
			}
			write.endArray();
		} else {
			write.element(rec.get(field));
		}
	}

	int compare(Object t1, Object t2) {
		if (t1 == null && t2 == null)
			return 0;
		if (t1 == null)
			return -1;
		if (t2 == null)
			return 1;
		if (t1 instanceof String)
			return getString(t1).compareTo(getString(t2));
		if (t1 instanceof Long)
			return Long.compare(getNumber(t1), getNumber(t2));
		if (t1 instanceof Double)
			return Double.compare(getFloat(t1), getFloat(t2));
		if (t1 instanceof Boolean || t2 instanceof Boolean) {
			if (getBoolean(t1) == getBoolean(t2))
				return 0;
			return getBoolean(t1) ? 1 : -1;
		}
		if (t1 instanceof RecordInterface && t2 instanceof RecordInterface) {
			RecordInterface r1 = (RecordInterface) t1;
			RecordInterface r2 = (RecordInterface) t2;
			if (r1.type() == FieldType.ARRAY && r2.type() == FieldType.ARRAY) {
				int f1 = r1.next(-1);
				int f2 = r2.next(-1);
				while (f1 >= 0 && f2 >= 0) {
					int c = compare(r1.get(f1), r2.get(f2));
					if (c != 0)
						return c;
					f1 = r1.next(f1);
					f2 = r2.next(f2);
					if (f1 < 0 && f2 >= 0)
						return -1;
					if (f2 < 0 && f1 >= 0)
						return 1;
				}
			}
		}
		return 0;
	}

	private Object doFunction(Operator code) {
		Object p1 = inter(code.getFnParm1());
		Object p2 = null;
		Function function = code.getFunction();
		if (function != Function.EACH && function != Function.FOR && function != Function.PER && function != Function.OR
				&& function != Function.AND && code.getFnParm2().getRec() != 0)
			p2 = inter(code.getFnParm2());
		switch (function) {
		case ADD:
			if (p1 instanceof String) {
				return ((String) p1) + getString(p2);
			} else if (p1 instanceof Long) {
				return ((Long) p1) + getNumber(p2);
			} else if (p1 instanceof Double) {
				return ((Double) p1) + getFloat(p2);
			} else if (p1 instanceof RecordInterface) {
				return new AddArray(this, (RecordInterface) p1, p2);
			}
			return null;
		case AND:
			if (!getBoolean(p1))
				return false;
			return inter(code.getFnParm2());
		case BOOLEAN:
			return getBoolean(p1);
		case PER:
			return new InterMap(this, code.getFnParm2(), p1);
		case FOR:
			if (p1 instanceof String)
				curFor = new StringArray((String) p1);
			else
				curFor = (RecordInterface) p1;
			Object resObj = inter(code.getFnParm2());
			return resObj;
		case EACH:
			running = p1;
			int pos = 1;
			while (curFor.type(pos) != null) {
				current = curFor.get(pos);
				RecordInterface remFor = curFor;
				running = inter(code.getFnParm2());
				curFor = remFor;
				pos = curFor.next(pos);
			}
			return running;
		case DIV:
			if (p1 instanceof Long) {
				long d = getNumber(p2);
				if (d == Long.MIN_VALUE || d == 0)
					return Long.MIN_VALUE;
				return ((Long) p1) / d;
			} else if (p1 instanceof Double) {
				double d = getFloat(p2);
				if (d == 0 || Double.isNaN(d))
					return null;
				return ((Double) p1) / d;
			}
			return null;
		case ELEMENT:
			if (p1 instanceof Operator && (((Operator) p1).getOperation() == Operation.OBJECT)) {
				Operator op = (Operator) p1;
				if (p2 instanceof Operator) {
					String fname = getString(inter((Operator) p2));
					for (ObjectArray fld : op.getObject()) {
						if (getString(inter(fld.getName())).equals(fname))
							return inter(fld);
					}
					return null;
				}
			} else if (p1 instanceof RecordInterface) {
				RecordInterface rec = (RecordInterface) p1;
				if (p2 instanceof Long) {
					long s = getNumber(p2);
					if (s == Long.MIN_VALUE || Math.abs(s) > Integer.MAX_VALUE)
						return null;
					if (s < 0)
						s += rec.getSize();
					if (rec.name(1 + (int) s) != null || rec.type(1 + (int) s) == null)
						return null;
					return rec.get(1 + (int) s);
				} else if (p2 instanceof String) {
					String name = getString(p2);
					if (name == null)
						return null;
					int res = rec.scanName(name);
					if (res == -1)
						return null;
					return rec.get(res);
				}
			} else if (p1 instanceof String) {
				long s = getNumber(p2);
				if (s == Long.MIN_VALUE || Math.abs(s) > Integer.MAX_VALUE)
					return null;
				String str = getString(p1);
				if (s < 0)
					s += str.length();
				if (s < 0 || s >= str.length())
					return null;
				return "" + str.charAt((int) s);
			}
			return null;
		case EQ:
			if (p1 instanceof Long)
				return (Long) p1 == getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 == getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).equals(getString(p2));
			else if (p1 instanceof Boolean)
				return ((Boolean) p1) == getBoolean(p2);
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) == 0;
			else if (p1 == null)
				return p2 == null;
			return false;
		case FIRST:
			return first;
		case FLOAT:
			return getFloat(p1);
		case GE:
			if (p1 instanceof Long)
				return (Long) p1 >= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 >= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) >= 0;
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) >= 0;
			return null;
		case GT:
			if (p1 instanceof Long)
				return (Long) p1 > getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 > getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) > 0;
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) > 0;
			return null;
		case INDEX:
			if (p1 instanceof String && p2 != null) {
				int indexOf = ((String) p1).indexOf(getString(p2));
				if (indexOf < 0)
					return null;
				return Long.valueOf(indexOf);
			}
			return Long.valueOf(index);
		case LAST:
			return last;
		case LE:
			if (p1 instanceof Long)
				return (Long) p1 <= getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 <= getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) <= 0;
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) <= 0;
			return null;
		case LENGTH:
			if (p1 == null && code.getFnParm1().getOperation() == Operation.ARRAY)
				return Long.valueOf(code.getFnParm1().getArray().getSize());
			if (p1 instanceof String)
				return Long.valueOf(((String) p1).length());
			if (p1 instanceof RecordInterface)
				return Long.valueOf(((RecordInterface) p1).getSize());
			return null;
		case NAME:
			return curName;
		case TYPE:
			if (p1 == null)
				return "NULL";
			else if (p1 instanceof Long)
				return "NUMBER";
			else if (p1 instanceof Double)
				return "FLOAT";
			else if (p1 instanceof String)
				return "STRING";
			else if (p1 instanceof Boolean)
				return "BOOLEAN";
			else if (p1 instanceof RecordInterface)
				return ((RecordInterface) p1).type().toString();
			return null;
		case LT:
			if (p1 instanceof Long)
				return (Long) p1 < getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 < getFloat(p2);
			else if (p1 instanceof String)
				return ((String) p1).compareTo(getString(p2)) < 0;
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) <= 0;
			return null;
		case MIN:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if ((Long) p1 == Long.MIN_VALUE || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 - number;
			} else if (p1 instanceof Double)
				return (Double) p1 - getFloat(p2);
			else if (p1 instanceof RecordInterface)
				return new MinArray((RecordInterface) p1, p2);
			return null;
		case MOD:
			if (p1 instanceof Long) {
				long number = getNumber(p2);
				if (number == 0 || number == Long.MIN_VALUE)
					return null;
				return ((Long) p1) % number;
			} else if (p1 instanceof Double) {
				double fl = getFloat(p2);
				if (fl == 0)
					return null;
				return (Double) p1 % fl;
			}
			return null;
		case MUL:
			if (p1 instanceof String) {
				long number = getNumber(p2);
				if (number == Long.MIN_VALUE || Math.abs(number) > Integer.MAX_VALUE)
					return null;
				return new String(new char[(int) number]).replace("\0", (String) p1);
			} else if (p1 instanceof Long) {
				long number = getNumber(p2);
				if ((Long) p1 == Long.MIN_VALUE || number == Long.MIN_VALUE)
					return null;
				return (Long) p1 * number;
			} else if (p1 instanceof Double) {
				return (Double) p1 * getFloat(p2);
			} else if (p1 instanceof RecordInterface) {
				return new InterMult(this, (RecordInterface) p1, getNumber(p2));
			}
			return null;
		case NE:
			if (p1 instanceof Long)
				return (Long) p1 != getNumber(p2);
			else if (p1 instanceof Double)
				return (Double) p1 != getFloat(p2);
			else if (p1 instanceof String)
				return !((String) p1).equals(getString(p2));
			else if (p1 instanceof Boolean)
				return ((Boolean) p1) != getBoolean(p2);
			else if (p1 instanceof RecordInterface && p2 instanceof RecordInterface)
				return compare(p1, p2) != 0;
			return false;
		case NEG:
			if (p1 instanceof Long)
				return -(Long) p1;
			else if (p1 instanceof Double)
				return -(Double) p1;
			return null;
		case NOT:
			return !getBoolean(p1);
		case NUMBER:
			return getNumber(p1);
		case OR:
			if (getBoolean(p1))
				return true;
			return inter(code.getFnParm2());
		case POW:
			if (p1 instanceof Long)
				return powLong((Long) p1, getNumber(p2));
			else if (p1 instanceof Double)
				return Math.pow((Double) p1, getFloat(p2));
			return null;
		case STRING:
			return getString(p1);
		case LAYOUT:
			return format((RecordInterface) p1, p2);
		default:
			break;
		}
		return null;
	}

	private String format(RecordInterface layout, Object obj) {
		if (obj instanceof Long || obj instanceof Double)
			return formatNumber(layout, obj);
		int width = -1;
		String align = "L";
		for (int fld = layout.next(-1); layout.type(fld) != null; fld = layout.next(fld)) {
			switch (layout.name(fld)) {
			case "width":
				Object w = layout.get(fld);
				if (w instanceof Operator)
					width = ((Long) inter((Operator) w)).intValue();
				else
					width = ((Long) w).intValue();
				break;
			case "align":
				align = (String) layout.get(fld);
				break;
			}
		}
		String res = getString(obj);
		if (align.equals("H")) {
			if (res.length() > width)
				return res.substring(0, width - 3) + "...";
			return res;
		} else if (align.equals("T")) {
			if (res.length() > width)
				return "..." + res.substring(res.length() - width + 3, res.length());
			return res;
		}
		if (width < 0 || width <= res.length())
			return res;
		int l = width - res.length();
		switch (align) {
		case "L":
			return res + multi(l, " ");
		case "R":
			return multi(l, " ") + res;
		case "C":
			int part = l / 2;
			return multi(part, " ") + res + multi(l - part, " ");
		}
		return null;
	}

	private String formatNumber(RecordInterface layout, Object obj) {
		int width = -1;
		int precision = -1;
		@SuppressWarnings("unused")
		String align = "R";
		String type = "g";
		String separator = null;
		boolean alternative = false;
		boolean leadingZero = false;
		for (int fld = layout.next(-1); layout.type(fld) != null; fld = layout.next(fld)) {
			switch (layout.name(fld)) {
			case "width":
				Object w = layout.get(fld);
				if (w instanceof Operator)
					width = ((Long) inter((Operator) w)).intValue();
				else
					width = ((Long) w).intValue();
				break;
			case "precision":
				Object p = layout.get(fld);
				if (p instanceof Operator)
					precision = ((Long) inter((Operator) p)).intValue();
				else
					precision = ((Long) p).intValue();
				break;
			case "align":
				align = (String) layout.get(fld);
				break;
			case "type":
				type = (String) layout.get(fld);
				break;
			case "separator":
				separator = (String) layout.get(fld);
				break;
			case "leadingZero":
				leadingZero = true;
				break;
			case "alternative":
				alternative = true;
				break;
			}
		}
		switch (type) {
		case "b": {
			String res = Long.toBinaryString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0b" : "") + separate(separator, res, 4);
			return (alternative ? "0b" : "") + res;
		}
		case "x": {
			String res = Long.toHexString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0x" : "") + separate(separator, res, 4);
			return (alternative ? "0x" : "") + res;
		}
		case "o": {
			String res = Long.toOctalString(getNumber(obj));
			if (width > res.length())
				res = multi(width - res.length(), leadingZero ? "0" : " ") + res;
			if (separator != null)
				return (alternative ? "0o" : "") + separate(separator, res, 3);
			return (alternative ? "0o" : "") + res;
		}
		}
		DecimalFormat formatter = null;
		if (type.equals("e"))
			formatter = (DecimalFormat) new DecimalFormat("0.#E0");
		else if (type.equals("g")) {
			if (obj instanceof Double) {
				int exp = Math.getExponent((Double) obj);
				if (exp < -6 || exp > 6)
					formatter = (DecimalFormat) new DecimalFormat("0.#E0");
			}
		}
		if (formatter == null)
			formatter = (DecimalFormat) NumberFormat.getInstance();
		DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
		symbols.setGroupingSeparator(',');
		symbols.setDecimalSeparator('.');
		symbols.setExponentSeparator("e");
		if (separator != null)
			symbols.setGroupingSeparator(separator.charAt(0));
		formatter.setDecimalFormatSymbols(symbols);
		if (obj instanceof Double) {
			formatter.setDecimalSeparatorAlwaysShown(true);
			formatter.setMinimumFractionDigits(1);
		}
		formatter.setMaximumFractionDigits(9);
		if (precision > 0) {
			formatter.setMaximumFractionDigits(precision);
			formatter.setMinimumFractionDigits(precision);
		}
		if (width > 0)
			formatter.setMinimumIntegerDigits(width);
		if (separator == null)
			formatter.setGroupingSize(0);
		else
			formatter.setGroupingSize(3);
		if (obj instanceof Long)
			return formatter.format((Long) obj);
		return formatter.format((Double) obj);
	}

	private String separate(String separator, String res, int spaces) {
		StringBuilder r = new StringBuilder();
		int i = res.length() % spaces;
		int l = 0;
		if (i == 0)
			i += 4;
		for (; i < res.length(); i += spaces) {
			r.append(res.substring(l, i));
			r.append(separator);
			l = i;
		}
		r.append(res.substring(l, res.length()));
		return r.toString();
	}

	private static String multi(int pos, String token) {
		return new String(new char[pos]).replace("\0", token);
	}

	private static long powLong(long number, long power) {
		long res = 1;
		long sq = number;
		while (power > 0) {
			if (power % 2 == 1)
				res *= sq;
			sq = sq * sq;
			power /= 2;
		}
		return res;
	}

	long getNumber(Object val) {
		if (val instanceof Long)
			return (Long) val;
		if (val instanceof Double)
			return Math.round((Double) val);
		if (val instanceof String) {
			try {
				return Long.parseLong((String) val);
			} catch (NumberFormatException e) {
				// nothing.. return Long.MIN_VALUE
			}
		}
		return Long.MIN_VALUE;
	}

	private String getString(Object val) {
		if (val == null)
			return "";
		if (val instanceof Long)
			return Long.toString((Long) val);
		else if (val instanceof Double)
			return Double.toString((Double) val);
		else if (val instanceof RecordInterface) {
			Writer write = new StringWriter();
			write.append(true);
			iterate(write, (RecordInterface) val);
			return write.toString();
		}
		return val.toString();
	}

	boolean getBoolean(Object val) {
		if (val instanceof Boolean)
			return (Boolean) val;
		if (val instanceof Long)
			return (Long) val != Long.MIN_VALUE;
		if (val instanceof Double)
			return !Double.isNaN((Double) val);
		return val != null;
	}

	private double getFloat(Object val) {
		if (val instanceof Long)
			return (Long) val;
		if (val instanceof Double)
			return (Double) val;
		if (val instanceof String) {
			try {
				return Double.parseDouble((String) val);
			} catch (NumberFormatException e) {
				// nothing.. return Double.NaN
			}
		}
		return Double.NaN;
	}

	public FieldType type(Object lastField) {
		if (lastField == null)
			return FieldType.NULL;
		if (lastField instanceof Integer)
			return FieldType.INTEGER;
		if (lastField instanceof Long)
			return FieldType.LONG;
		if (lastField instanceof Double)
			return FieldType.FLOAT;
		if (lastField instanceof String)
			return FieldType.STRING;
		if (lastField instanceof Date)
			return FieldType.DATE;
		if (lastField instanceof Boolean)
			return FieldType.BOOLEAN;
		if (lastField instanceof RecordInterface)
			return ((RecordInterface) lastField).type();
		throw new RuntimeException("Unknown type");
	}
}
