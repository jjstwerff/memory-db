package org.memorydb.jslt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.memorydb.handler.StringWriter;
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
		case FOR:
			return new InterMap(this, code.getForExpr(), inter(code.getFor()));
		case FUNCTION:
			return doFunction(code);
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
		System.out.println("macro:" + macro.getName() + " stack:" + stack);
		for (Alternative alt : macro.getAlternatives()) {
			if (alt.getParameters().getSize() != parms)
				continue;
			int pnr = 0;
			boolean found = true;
			for (ParametersArray parm : alt.getParameters()) {
				Object obj = stack.get(stackF + pnr);
				switch (parm.getType()) {
				case BOOLEAN:
					if (!(obj instanceof Boolean) || (Boolean) obj != parm.isBoolean())
						found = false;
					break;
				case FLOAT:
					if (!(obj instanceof Double) || (Double) obj != parm.getFloat())
						found = false;
					break;
				case NULL:
					if (obj != null)
						found = false;
					break;
				case NUMBER:
					if (!(obj instanceof Long) || (Long) obj != parm.getNumber())
						found = false;
					break;
				case OBJECT:
					if (!(obj instanceof RecordInterface) || ((RecordInterface) obj).type() != FieldType.OBJECT)
						found = false;
					break;
				case ARRAY:
					if (!(obj instanceof RecordInterface) || ((RecordInterface) obj).type() != FieldType.ARRAY)
						found = false;
					break;
				case STRING:
					if (!(obj instanceof String) || !parm.getString().equals(obj))
						found = false;
					break;
				case VARIABLE:
					if (!matches(obj, parm.getVariable().getType()))
						found = false;
					break;
				default:
					break;
				}
				pnr++;
			}
			if (found) {
				pnr = 0;
				int lastFrame = stackFrame;
				stackFrame = stack.size();
				for (ParametersArray parm : alt.getParameters()) {
					if (parm.getType() == Match.Type.VARIABLE)
						stack.add(stack.get(stackF + pnr));
					pnr++;
				}
				System.out.println("alt:" + macro.getName() + " frameFrame:" + stackFrame + " on stack:" + stack);
				Object res = inter(alt.getCode().iterator().next());
				while (stack.size() > stackFrame)
					stack.remove(stack.size() - 1);
				stackFrame = lastFrame;
				return res;
			}
			throw new RuntimeException("Could not find alternative for " + macro.getName());
		}
		return null;
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
		if (maxDepth <= 0 || !rec.exists())
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
		if (function != Function.OR && function != Function.AND && code.getFnParm2().getRec() != 0)
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
				return indexOf;
			}
			return index;
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
				return code.getFnParm1().getArray().getSize();
			if (p1 instanceof String)
				return ((String) p1).length();
			if (p1 instanceof RecordInterface)
				return ((RecordInterface) p1).getSize();
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
				return (Long) p1 % number;
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
		default:
			break;
		}
		return null;
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
